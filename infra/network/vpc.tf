resource "aws_vpc" "turip" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = { Name = "turip-vpc" }
}

resource "aws_subnet" "turip" {
  vpc_id            = aws_vpc.turip.id
  cidr_block        = var.subnet_cidr
  availability_zone = var.availability_zone

  tags = { Name = "turip-subnet" }
}

resource "aws_internet_gateway" "turip" {
  vpc_id = aws_vpc.turip.id

  tags = { Name = "turip-igw" }
}

resource "aws_route_table" "turip" {
  vpc_id = aws_vpc.turip.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.turip.id
  }

  tags = { Name = "turip-rt" }
}

resource "aws_route_table_association" "turip" {
  subnet_id      = aws_subnet.turip.id
  route_table_id = aws_route_table.turip.id
}
