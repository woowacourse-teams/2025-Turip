resource "aws_s3_bucket" "turip" {
  bucket = var.bucket_name

  tags = { Name = var.bucket_name }
}

resource "aws_s3_bucket_public_access_block" "turip" {
  bucket = aws_s3_bucket.turip.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "turip" {
  bucket = aws_s3_bucket.turip.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowPublicRead"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.turip.arn}/*"
      },
      {
        Sid    = "AllowUploadFromAppRole"
        Effect = "Allow"
        Principal = {
          AWS = aws_iam_role.s3_upload.arn
        }
        Action   = "s3:PutObject"
        Resource = "${aws_s3_bucket.turip.arn}/*"
      }
    ]
  })

  depends_on = [aws_s3_bucket_public_access_block.turip]
}
