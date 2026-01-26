-- 기본값으로 추가해줬던 email을 이메일 형식으로 수정

UPDATE member
SET email = 'unknown@turip.com'
WHERE email = 'unknown';
