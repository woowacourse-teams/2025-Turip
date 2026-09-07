variable "region" {
  type    = string
  default = "ap-northeast-2"
}

variable "aws_profile" {
  type    = string
  default = "turip"
}

variable "discord_public_key" {
  type      = string
  sensitive = true
}

variable "discord_bot_token" {
  type      = string
  sensitive = true
}

variable "schedule_name" {
  type    = string
  default = "dev-server-stop"
}
