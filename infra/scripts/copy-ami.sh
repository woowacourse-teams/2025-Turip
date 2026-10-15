#!/usr/bin/env bash
set -euo pipefail

OLD_PROFILE="${OLD_PROFILE:?구계정 프로파일을 OLD_PROFILE 로 지정하세요}"
NEW_PROFILE="${NEW_PROFILE:?새계정 프로파일을 NEW_PROFILE 로 지정하세요}"
REGION="${REGION:-ap-northeast-2}"

if [ "$#" -lt 1 ]; then
  echo "사용법: OLD_PROFILE=.. NEW_PROFILE=.. ./copy-ami.sh <old-ami-id> [<old-ami-id> ...]"
  exit 1
fi

NEW_ACCOUNT_ID="$(aws sts get-caller-identity --profile "$NEW_PROFILE" --query Account --output text)"
echo "새 계정 ID: $NEW_ACCOUNT_ID"

for OLD_AMI in "$@"; do
  echo "=============================================="
  echo "[$OLD_AMI] 처리 중..."

  SNAP_IDS="$(aws ec2 describe-images \
    --profile "$OLD_PROFILE" --region "$REGION" \
    --image-ids "$OLD_AMI" \
    --query 'Images[0].BlockDeviceMappings[].Ebs.SnapshotId' \
    --output text)"

  aws ec2 modify-image-attribute \
    --profile "$OLD_PROFILE" --region "$REGION" \
    --image-id "$OLD_AMI" \
    --launch-permission "Add=[{UserId=$NEW_ACCOUNT_ID}]"

  for SNAP in $SNAP_IDS; do
    aws ec2 modify-snapshot-attribute \
      --profile "$OLD_PROFILE" --region "$REGION" \
      --snapshot-id "$SNAP" \
      --attribute createVolumePermission \
      --operation-type add --user-ids "$NEW_ACCOUNT_ID"
    echo "  공유한 스냅샷: $SNAP"
  done

  NEW_AMI="$(aws ec2 copy-image \
    --profile "$NEW_PROFILE" --region "$REGION" \
    --source-region "$REGION" \
    --source-image-id "$OLD_AMI" \
    --name "turip-copy-$OLD_AMI" \
    --query ImageId --output text)"

  echo "  → 새 계정 AMI: $NEW_AMI  (구: $OLD_AMI)"
done

echo "=============================================="
echo "상태 확인: aws ec2 describe-images --profile $NEW_PROFILE --region $REGION --owners self --query 'Images[].[ImageId,Name,State]' --output table"
