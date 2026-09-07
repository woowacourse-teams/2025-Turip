resource "aws_iam_role" "s3_upload" {
  name = "turip-s3-upload-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = { Name = "turip-s3-upload-role" }
}

resource "aws_iam_role_policy" "s3_upload" {
  name = "turip-s3-upload-policy"
  role = aws_iam_role.s3_upload.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
        ]
        Resource = "${aws_s3_bucket.turip.arn}/*"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "s3_upload" {
  name = "turip-s3-upload-profile"
  role = aws_iam_role.s3_upload.name
}
