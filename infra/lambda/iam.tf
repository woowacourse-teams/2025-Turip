data "aws_caller_identity" "current" {}

locals {
  account_id       = data.aws_caller_identity.current.account_id
  function_name    = "dev-server-controller"
  function_arn     = "arn:aws:lambda:${var.region}:${local.account_id}:function:${local.function_name}"
  dev_instance_id  = data.terraform_remote_state.compute.outputs.instance_ids["dev"]
  dev_instance_arn = "arn:aws:ec2:${var.region}:${local.account_id}:instance/${local.dev_instance_id}"
  schedule_arn     = "arn:aws:scheduler:${var.region}:${local.account_id}:schedule/default/${var.schedule_name}"
}

resource "aws_iam_role" "lambda" {
  name = "dev-server-lambda-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = { Name = "dev-server-lambda-role" }
}

resource "aws_iam_role_policy" "lambda" {
  name = "dev-ec2-eventbridge"
  role = aws_iam_role.lambda.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid      = "EC2Describe"
        Effect   = "Allow"
        Action   = "ec2:DescribeInstances"
        Resource = "*"
      },
      {
        Sid      = "GetDiscordBotToken"
        Effect   = "Allow"
        Action   = "secretsmanager:GetSecretValue"
        Resource = aws_secretsmanager_secret.discord_bot_token.arn
      },
      {
        Sid    = "EC2StartStop"
        Effect = "Allow"
        Action = [
          "ec2:StartInstances",
          "ec2:StopInstances",
        ]
        Resource = local.dev_instance_arn
      },
      {
        Sid    = "SchedulerManage"
        Effect = "Allow"
        Action = [
          "scheduler:CreateSchedule",
          "scheduler:UpdateSchedule",
          "scheduler:DeleteSchedule",
          "scheduler:GetSchedule",
        ]
        Resource = local.schedule_arn
      },
      {
        Sid      = "PassRoleToScheduler"
        Effect   = "Allow"
        Action   = "iam:PassRole"
        Resource = aws_iam_role.scheduler_invoke.arn
      },
      {
        Sid    = "Logs"
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
        ]
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Sid      = "InvokeSelf"
        Effect   = "Allow"
        Action   = "lambda:InvokeFunction"
        Resource = local.function_arn
      },
    ]
  })
}

resource "aws_iam_role" "scheduler_invoke" {
  name = "dev-server-scheduler-invoke-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "scheduler.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = { Name = "dev-server-scheduler-invoke-role" }
}

resource "aws_iam_role_policy" "scheduler_invoke" {
  name = "invoke-dev-server-controller"
  role = aws_iam_role.scheduler_invoke.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = "lambda:InvokeFunction"
        Resource = local.function_arn
      }
    ]
  })
}
