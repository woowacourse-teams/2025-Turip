-- 게스트 닉네임 형식 변경: "게스트_랜덤숫자" 형식으로 통일

UPDATE account a
INNER JOIN guest g ON a.id = g.account_id
SET a.nickname = CONCAT('게스트_', 50000 + a.id)
WHERE a.nickname IS NULL
   OR a.nickname NOT LIKE '게스트\_%';
