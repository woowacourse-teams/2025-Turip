terraform {
  required_version = ">= 1.10.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    archive = {
      source  = "hashicorp/archive"
      version = "~> 2.4"
    }
  }

  backend "s3" {
    bucket       = "turip-tf"
    key          = "lambda/terraform.tfstate"
    region       = "ap-northeast-2"
    profile      = "turip"
    encrypt      = true
    use_lockfile = true
  }
}

provider "aws" {
  region  = var.region
  profile = var.aws_profile
}

data "terraform_remote_state" "compute" {
  backend = "s3"

  config = {
    bucket  = "turip-tf"
    key     = "compute/terraform.tfstate"
    region  = "ap-northeast-2"
    profile = var.aws_profile
  }
}
