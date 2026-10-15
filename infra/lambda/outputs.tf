output "function_url" {
  value = aws_lambda_function_url.dev_server_controller.function_url
}

output "function_arn" {
  value = aws_lambda_function.dev_server_controller.arn
}
