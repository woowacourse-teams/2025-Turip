output "vpc_id" {
  value = aws_vpc.turip.id
}

output "subnet_id" {
  value = aws_subnet.turip.id
}

output "was_sg_id" {
  value = aws_security_group.was.id
}

output "db_sg_id" {
  value = aws_security_group.db.id
}
