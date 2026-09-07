data "archive_file" "dev_server_controller" {
  type        = "zip"
  source_dir  = "${path.module}/src"
  output_path = "${path.module}/build/dev-server-controller.zip"
}

resource "aws_lambda_layer_version" "pynacl" {
  layer_name          = "PyNaCl"
  filename            = "${path.module}/layers/pynacl-layer.zip"
  source_code_hash    = filebase64sha256("${path.module}/layers/pynacl-layer.zip")
  compatible_runtimes = ["python3.12"]
}

resource "aws_lambda_function" "dev_server_controller" {
  function_name = local.function_name
  role          = aws_iam_role.lambda.arn
  handler       = "lambda_function.lambda_handler"
  runtime       = "python3.12"
  timeout       = 15
  memory_size   = 128

  filename         = data.archive_file.dev_server_controller.output_path
  source_code_hash = data.archive_file.dev_server_controller.output_base64sha256

  layers = [aws_lambda_layer_version.pynacl.arn]

  environment {
    variables = {
      DISCORD_PUBLIC_KEY = var.discord_public_key
      DISCORD_BOT_TOKEN  = var.discord_bot_token
      INSTANCE_ID        = local.dev_instance_id
      SCHEDULER_ROLE_ARN = aws_iam_role.scheduler_invoke.arn
      TARGET_LAMBDA_ARN  = local.function_arn
      SCHEDULE_NAME      = var.schedule_name
      AWS_REGION_NAME    = var.region
    }
  }

  tags = { Name = local.function_name }
}

resource "aws_lambda_function_url" "dev_server_controller" {
  function_name      = aws_lambda_function.dev_server_controller.function_name
  authorization_type = "NONE"
}

resource "aws_lambda_permission" "public_url_invoke" {
  statement_id           = "FunctionURLAllowPublicAccess"
  action                 = "lambda:InvokeFunctionUrl"
  function_name          = aws_lambda_function.dev_server_controller.function_name
  principal              = "*"
  function_url_auth_type = "NONE"
}

# 두 번째 resource-based policy statement(FunctionURLAllowInvokeAction, action=lambda:InvokeFunction,
# condition=lambda:InvokedViaFunctionUrl)는 aws_lambda_permission 리소스(hashicorp/aws ~> 5.100 포함)가
# invoked-via-function-url 조합을 지원하지 않아 Terraform으로 표현 불가.
# 이 statement 없이는 Function URL 호출이 403(AccessDeniedException)으로 막힌다.
# 아래 AWS CLI로 수동 추가함 (Terraform state 밖에 있으므로 plan/destroy에 나타나지 않음):
#   aws lambda add-permission \
#     --function-name dev-server-controller \
#     --statement-id FunctionURLAllowInvokeAction \
#     --action lambda:InvokeFunction \
#     --principal '*' \
#     --invoked-via-function-url

