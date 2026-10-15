output "instance_ids" {
  value = { for name, inst in aws_instance.turip : name => inst.id }
}

output "private_ips" {
  value = { for name, inst in aws_instance.turip : name => inst.private_ip }
}

output "public_ips" {
  value = { for name, eip in aws_eip.turip : name => eip.public_ip }
}
