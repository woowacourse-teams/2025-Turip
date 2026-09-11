#!/usr/bin/env bash
set -euo pipefail

PROFILE="${PROFILE:-turip}"
REGION="${REGION:-ap-northeast-2}"
BUCKET="${BUCKET:-turip-tf}"

echo "state 버킷 생성: s3://$BUCKET ($PROFILE / $REGION)"

if aws s3api head-bucket --bucket "$BUCKET" --profile "$PROFILE" 2>/dev/null; then
  echo "이미 존재함. 건너뜀."
else
  aws s3api create-bucket \
    --bucket "$BUCKET" \
    --profile "$PROFILE" \
    --region "$REGION" \
    --create-bucket-configuration LocationConstraint="$REGION"
  echo "생성 완료."
fi

aws s3api put-bucket-versioning \
  --bucket "$BUCKET" \
  --profile "$PROFILE" \
  --versioning-configuration Status=Enabled
echo "버전 관리 활성화."

aws s3api put-bucket-encryption \
  --bucket "$BUCKET" \
  --profile "$PROFILE" \
  --server-side-encryption-configuration \
    '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
echo "기본 암호화 활성화."

echo "완료."
