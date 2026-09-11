#!/usr/bin/env bash
set -euo pipefail

OLD_PROFILE="${OLD_PROFILE:?구계정 프로파일을 OLD_PROFILE 로 지정하세요}"
NEW_PROFILE="${NEW_PROFILE:-turip}"
OLD_BUCKET="${OLD_BUCKET:-turip-bucket-2}"
NEW_BUCKET="${NEW_BUCKET:-turip-bucket-3}"
REGION="${REGION:-ap-northeast-2}"

# turip-bucket-2 정책은 GetObject/PutObject만 퍼블릭 허용하고 ListBucket은 막혀 있어
# 대상 계정(turip) 자격으로는 목록 조회가 안 되므로, 소유 계정(turip1) 자격으로
# 로컬에 내려받은 뒤 대상 계정(turip) 자격으로 업로드하는 2단계로 진행한다.
WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

echo "1/2 다운로드: s3://$OLD_BUCKET ($OLD_PROFILE) → $WORKDIR"
echo "=============================================="
aws s3 sync "s3://$OLD_BUCKET" "$WORKDIR" \
  --region "$REGION" \
  --profile "$OLD_PROFILE"

echo "=============================================="
echo "2/2 업로드: $WORKDIR → s3://$NEW_BUCKET ($NEW_PROFILE)"
echo "=============================================="

echo "[dry-run] 변경 예정 목록:"
aws s3 sync "$WORKDIR" "s3://$NEW_BUCKET" \
  --region "$REGION" \
  --profile "$NEW_PROFILE" \
  --dryrun

echo "=============================================="
read -r -p "위 목록대로 실제 업로드를 진행할까요? [y/N] " CONFIRM
if [[ "$CONFIRM" != "y" && "$CONFIRM" != "Y" ]]; then
  echo "취소했습니다."
  exit 0
fi

aws s3 sync "$WORKDIR" "s3://$NEW_BUCKET" \
  --region "$REGION" \
  --profile "$NEW_PROFILE"

echo "=============================================="
echo "완료. 확인: aws s3 ls s3://$NEW_BUCKET --profile $NEW_PROFILE --recursive --summarize | tail -n 5"
