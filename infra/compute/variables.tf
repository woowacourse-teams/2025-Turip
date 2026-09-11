variable "region" {
  type    = string
  default = "ap-northeast-2"
}

variable "aws_profile" {
  type    = string
  default = "turip"
}

variable "key_name" {
  type    = string
  default = "turip3"
}

variable "instances" {
  type = map(object({
    ami_id         = string
    private_ip     = string
    instance_type  = string
    security_group = string # "was" or "db"
    assign_eip     = bool
    attach_s3_role = bool
  }))
}
