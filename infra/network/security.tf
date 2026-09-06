resource "aws_security_group" "was" {
  name        = "was-sg-turip"
  description = "WAS security group for Turip"
  vpc_id      = aws_vpc.turip.id

  tags = { Name = "was-sg-turip" }
}

resource "aws_security_group_rule" "was_web" {
  for_each          = toset(["80", "443", "8080", "8081"])
  type              = "ingress"
  from_port         = tonumber(each.value)
  to_port           = tonumber(each.value)
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.was.id
  description       = "web ${each.value} from anywhere"
}

resource "aws_security_group_rule" "was_self" {
  type                     = "ingress"
  from_port                = 0
  to_port                  = 0
  protocol                 = "-1"
  source_security_group_id = aws_security_group.was.id
  security_group_id        = aws_security_group.was.id
  description              = "all traffic from was-sg-turip"
}

resource "aws_security_group_rule" "was_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.was.id
  description       = "all outbound"
}

resource "aws_security_group" "db" {
  name        = "db-sg-turip"
  description = "DB security group for Turip"
  vpc_id      = aws_vpc.turip.id

  tags = { Name = "db-sg-turip" }
}

resource "aws_security_group_rule" "db_mysql" {
  for_each                 = toset(["3306", "3307"])
  type                     = "ingress"
  from_port                = tonumber(each.value)
  to_port                  = tonumber(each.value)
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.was.id
  security_group_id        = aws_security_group.db.id
  description              = "mysql ${each.value} from was-sg-turip"
}

resource "aws_security_group_rule" "db_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.db.id
  description       = "all outbound"
}
