output "bucket_name" {
  value = aws_s3_bucket.turip.bucket
}

output "bucket_arn" {
  value = aws_s3_bucket.turip.arn
}

output "s3_upload_instance_profile_name" {
  value = aws_iam_instance_profile.s3_upload.name
}
