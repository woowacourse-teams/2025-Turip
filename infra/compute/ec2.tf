locals {
  sg_id_by_name = {
    was = data.terraform_remote_state.network.outputs.was_sg_id
    db  = data.terraform_remote_state.network.outputs.db_sg_id
  }
}

resource "aws_instance" "turip" {
  for_each = var.instances

  ami                    = each.value.ami_id
  instance_type          = each.value.instance_type
  subnet_id              = data.terraform_remote_state.network.outputs.subnet_id
  private_ip             = each.value.private_ip
  vpc_security_group_ids = [local.sg_id_by_name[each.value.security_group]]
  key_name               = var.key_name

  iam_instance_profile = each.value.attach_s3_role ? data.terraform_remote_state.storage.outputs.s3_upload_instance_profile_name : null

  tags = { Name = "turip-${each.key}" }
}

resource "aws_eip" "turip" {
  for_each = { for name, cfg in var.instances : name => cfg if cfg.assign_eip }

  domain = "vpc"

  tags = { Name = "turip-${each.key}-eip" }
}

resource "aws_eip_association" "turip" {
  for_each = aws_eip.turip

  instance_id   = aws_instance.turip[each.key].id
  allocation_id = each.value.id
}
