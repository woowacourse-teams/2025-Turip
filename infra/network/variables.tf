variable "region" {
  type    = string
  default = "ap-northeast-2"
}

variable "aws_profile" {
  type    = string
  default = "turip"
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/24"
}

variable "subnet_cidr" {
  type    = string
  default = "10.0.0.0/24"
}

variable "availability_zone" {
  type    = string
  default = "ap-northeast-2a"
}
