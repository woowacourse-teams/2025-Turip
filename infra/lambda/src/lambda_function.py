"""
Lambda 함수: dev-server-controller
런타임: Python 3.12
핸들러: lambda_function.lambda_handler

환경 변수:
  DISCORD_PUBLIC_KEY        - Discord General Information의 Public Key
  DISCORD_BOT_TOKEN_SECRET_ARN - Discord Bot Token이 저장된 Secrets Manager 시크릿 ARN (후속 webhook 메시지 전송용)
  INSTANCE_ID               - 개발서버 EC2 인스턴스 ID (i-xxxx)
  SCHEDULER_ROLE_ARN        - dev-server-scheduler-invoke-role의 ARN
  TARGET_LAMBDA_ARN         - 이 람다 자신의 ARN (stop 액션을 자기 자신에게 다시 호출시키기 위함)
  SCHEDULE_NAME             - 예: dev-server-stop
  AWS_REGION_NAME           - 예: ap-northeast-2

[중요] dev-server-lambda-role 에 아래 권한도 추가로 필요함 (자기 자신을 비동기 재호출하기 위함):
  {
    "Sid": "InvokeSelf",
    "Effect": "Allow",
    "Action": "lambda:InvokeFunction",
    "Resource": "arn:aws:lambda:ap-northeast-2:계정ID:function:dev-server-controller"
  }

Lambda Layer 필요: PyNaCl (서명 검증용) -> pip install pynacl -t python/ 으로 레이어 생성 후 추가
함수 URL 또는 API Gateway 둘 다 PUBLIC, POST 허용으로 설정.
"""

import os
import json
import time
import boto3
from datetime import datetime, timedelta, timezone
from nacl.signing import VerifyKey
from nacl.exceptions import BadSignatureError
import urllib.request

DISCORD_PUBLIC_KEY = os.environ["DISCORD_PUBLIC_KEY"]
DISCORD_BOT_TOKEN_SECRET_ARN = os.environ["DISCORD_BOT_TOKEN_SECRET_ARN"]
INSTANCE_ID = os.environ["INSTANCE_ID"]
SCHEDULER_ROLE_ARN = os.environ["SCHEDULER_ROLE_ARN"]
TARGET_LAMBDA_ARN = os.environ["TARGET_LAMBDA_ARN"]
SCHEDULE_NAME = os.environ.get("SCHEDULE_NAME", "dev-server-stop")
REGION = os.environ.get("AWS_REGION_NAME", "ap-northeast-2")

ec2 = boto3.client("ec2", region_name=REGION)
scheduler = boto3.client("scheduler", region_name=REGION)
secretsmanager = boto3.client("secretsmanager", region_name=REGION)

VERIFY_KEY = VerifyKey(bytes.fromhex(DISCORD_PUBLIC_KEY))
DISCORD_BOT_TOKEN = secretsmanager.get_secret_value(
    SecretId=DISCORD_BOT_TOKEN_SECRET_ARN
)["SecretString"]


# ---------- 공통 유틸 ----------

def get_raw_body(event):
    body = event.get("body", "") or ""
    if event.get("isBase64Encoded"):
        import base64
        body = base64.b64decode(body).decode("utf-8")
    return body


def verify_discord_signature(event):
    headers = {k.lower(): v for k, v in (event.get("headers") or {}).items()}
    signature = headers.get("x-signature-ed25519", "")
    timestamp = headers.get("x-signature-timestamp", "")
    body = get_raw_body(event)
    try:
        VERIFY_KEY.verify(f"{timestamp}{body}".encode(), bytes.fromhex(signature))
        return True
    except (BadSignatureError, ValueError):
        return False


def get_instance_state():
    res = ec2.describe_instances(InstanceIds=[INSTANCE_ID])
    return res["Reservations"][0]["Instances"][0]["State"]["Name"]


def start_instance():
    ec2.start_instances(InstanceIds=[INSTANCE_ID])


def stop_instance():
    ec2.stop_instances(InstanceIds=[INSTANCE_ID])


KST = timezone(timedelta(hours=9))


def to_kst_str(utc_naive_str):
    """'2026-06-30T13:00:36' 형식(UTC, naive)을 KST 문자열로 변환."""
    dt_utc = datetime.fromisoformat(utc_naive_str).replace(tzinfo=timezone.utc)
    dt_kst = dt_utc.astimezone(KST)
    return dt_kst.strftime("%Y-%m-%d %H:%M:%S (KST)")


def upsert_stop_schedule(minutes=60):
    """minutes 뒤에 stop을 실행하도록 일회성 스케줄 등록(있으면 갱신)."""
    run_at = (datetime.now(timezone.utc) + timedelta(minutes=minutes)).strftime("%Y-%m-%dT%H:%M:%S")
    payload = json.dumps({"action": "scheduled_stop"})

    params = dict(
        Name=SCHEDULE_NAME,
        ScheduleExpression=f"at({run_at})",
        FlexibleTimeWindow={"Mode": "OFF"},
        Target={
            "Arn": TARGET_LAMBDA_ARN,
            "RoleArn": SCHEDULER_ROLE_ARN,
            "Input": payload,
        },
        ActionAfterCompletion="DELETE",  # 실행 후 스케줄 자동 삭제
    )

    try:
        scheduler.update_schedule(**params)
    except scheduler.exceptions.ResourceNotFoundException:
        scheduler.create_schedule(**params)

    return run_at


def delete_stop_schedule():
    try:
        scheduler.delete_schedule(Name=SCHEDULE_NAME)
    except scheduler.exceptions.ResourceNotFoundException:
        pass


def get_schedule_remaining():
    try:
        res = scheduler.get_schedule(Name=SCHEDULE_NAME)
        expr = res["ScheduleExpression"]  # at(2026-06-30T12:00:00)
        ts = expr[3:-1]
        run_at = datetime.fromisoformat(ts).replace(tzinfo=timezone.utc)
        remaining = run_at - datetime.now(timezone.utc)
        minutes_left = max(int(remaining.total_seconds() // 60), 0)
        return minutes_left, ts
    except scheduler.exceptions.ResourceNotFoundException:
        return None


def send_followup(application_id, interaction_token, content):
    url = f"https://discord.com/api/v10/webhooks/{application_id}/{interaction_token}"
    body = json.dumps({"content": content}).encode()
    req = urllib.request.Request(
        url,
        data=body,
        headers={
            "Content-Type": "application/json",
            "User-Agent": "DevServerBot (https://example.com, 1.0)",
        },
        method="POST",
    )
    try:
        urllib.request.urlopen(req)
    except Exception as e:
        # followup 전송 자체가 실패해도 최소한 로그는 남긴다
        print(f"send_followup failed: {e}")


# ---------- 명령어 처리 ----------

def handle_dev_on(application_id, token):
    state = get_instance_state()
    if state in ("running", "pending"):
        run_at = upsert_stop_schedule(60)
        send_followup(application_id, token, f"이미 켜져 있어요. 자동 종료 시각을 {to_kst_str(run_at)}로 갱신했습니다 (1시간 연장).")
        return

    start_instance()
    run_at = upsert_stop_schedule(60)
    send_followup(application_id, token, f"개발 서버를 켰습니다. 1시간 뒤({to_kst_str(run_at)}) 자동으로 꺼집니다. 부팅까지 1~2분 정도 걸려요.")


def handle_dev_off(application_id, token):
    delete_stop_schedule()
    stop_instance()
    send_followup(application_id, token, "개발 서버를 껐습니다.")


def handle_dev_status(application_id, token):
    state = get_instance_state()
    schedule_info = get_schedule_remaining()
    if state in ("running", "pending") and schedule_info is not None:
        minutes_left, run_at_ts = schedule_info
        hours, mins = divmod(minutes_left, 60)
        if hours > 0:
            remain_str = f"{hours}시간 {mins}분"
        else:
            remain_str = f"{mins}분"
        send_followup(
            application_id,
            token,
            f"현재 상태: {state}\n자동 종료까지: 약 {remain_str} 남음\n종료 예정 시각: {to_kst_str(run_at_ts)}",
        )
    else:
        send_followup(application_id, token, f"현재 상태: {state}")


def handle_dev_extend(application_id, token):
    state = get_instance_state()
    if state not in ("running", "pending"):
        send_followup(application_id, token, "서버가 꺼져 있어서 연장할 수 없어요. /dev on 으로 먼저 켜주세요.")
        return
    run_at = upsert_stop_schedule(60)
    send_followup(application_id, token, f"1시간 연장했습니다. 자동 종료 시각: {to_kst_str(run_at)}")


# ---------- 엔트리포인트 ----------

lambda_client = boto3.client("lambda", region_name=REGION)


def lambda_handler(event, context):
    # 1) 스케줄러가 직접 호출한 경우 (정기 stop 실행)
    if isinstance(event, dict) and event.get("action") == "scheduled_stop":
        stop_instance()
        return {"ok": True}

    # 2) 자기 자신을 비동기로 재호출한 경우 (실제 작업 처리용)
    if isinstance(event, dict) and event.get("action") == "process_command":
        sub_command = event["sub_command"]
        application_id = event["application_id"]
        token = event["token"]
        try:
            if sub_command == "on":
                handle_dev_on(application_id, token)
            elif sub_command == "off":
                handle_dev_off(application_id, token)
            elif sub_command == "status":
                handle_dev_status(application_id, token)
            elif sub_command == "extend":
                handle_dev_extend(application_id, token)
        except Exception as e:
            import traceback
            print("handler error:", traceback.format_exc())
            send_followup(application_id, token, f"에러 발생: {e}")
        return {"ok": True}

    # 3) Discord Interactions 요청 처리 (HTTP 진입점)
    if not verify_discord_signature(event):
        return {"statusCode": 401, "body": "invalid request signature"}

    body = json.loads(get_raw_body(event) or "{}")
    interaction_type = body.get("type")

    # PING (디스코드가 엔드포인트 등록 시 보내는 확인 요청)
    if interaction_type == 1:
        return {"statusCode": 200, "headers": {"Content-Type": "application/json"}, "body": json.dumps({"type": 1})}

    # APPLICATION_COMMAND
    if interaction_type == 2:
        application_id = body["application_id"]
        token = body["token"]
        sub_command = body["data"]["options"][0]["name"]  # on / off / status / extend

        # 실제 작업은 자기 자신을 비동기(Event)로 재호출해서 처리 -> 여기선 즉시 응답만 반환
        lambda_client.invoke(
            FunctionName=context.invoked_function_arn,
            InvocationType="Event",
            Payload=json.dumps({
                "action": "process_command",
                "sub_command": sub_command,
                "application_id": application_id,
                "token": token,
            }),
        )

        return {
            "statusCode": 200,
            "headers": {"Content-Type": "application/json"},
            "body": json.dumps({"type": 5}),  # DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE
        }

    return {"statusCode": 400, "body": "unhandled interaction type"}
