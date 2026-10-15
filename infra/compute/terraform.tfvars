instances = {
  monitoring = {
    ami_id         = "ami-0c315e80c1354d981"
    private_ip     = "10.0.0.209"
    instance_type  = "t4g.micro"
    security_group = "was"
    assign_eip     = true
    attach_s3_role = false
  }
  dev = {
    ami_id         = "ami-0394d982e301856ba"
    private_ip     = "10.0.0.103"
    instance_type  = "t4g.micro"
    security_group = "was"
    assign_eip     = true
    attach_s3_role = true
  }
  prod = {
    ami_id         = "ami-0b7a39a2d3efac4cb"
    private_ip     = "10.0.0.183"
    instance_type  = "t4g.small"
    security_group = "was"
    assign_eip     = true
    attach_s3_role = true
  }
  prod-db = {
    ami_id         = "ami-0faf5f62d432bbbc7"
    private_ip     = "10.0.0.111"
    instance_type  = "t4g.micro"
    security_group = "db"
    assign_eip     = false
    attach_s3_role = true
  }
}
