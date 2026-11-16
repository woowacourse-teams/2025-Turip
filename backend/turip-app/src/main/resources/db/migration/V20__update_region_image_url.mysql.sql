-- country 이미지 URL 업데이트 (OverseasRegionCategory 대상 국가)
UPDATE country
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/overseas/big/japan'
WHERE name = '일본';

UPDATE country
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/overseas/big/china'
WHERE name = '중국';

UPDATE country
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/overseas/big/vietnam'
WHERE name = '베트남';

UPDATE country
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/overseas/big/taiwan'
WHERE name = '대만';

-- city 이미지 URL 업데이트 (DomesticRegionCategory 대상 도시)
UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/seoul'
WHERE name = '서울';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/busan'
WHERE name = '부산';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/jeju'
WHERE name = '제주';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/incheon'
WHERE name = '인천';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/daejeon'
WHERE name = '대전';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/jeonju'
WHERE name = '전주';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/gangneung'
WHERE name = '강릉';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/sokcho'
WHERE name = '속초';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/gyeongju'
WHERE name = '경주';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/gongju'
WHERE name = '공주';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/yeosu'
WHERE name = '여수';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/suwon'
WHERE name = '수원';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/gunsan'
WHERE name = '군산';

UPDATE city
SET image_url = 'https://turip-bucket-3.s3.ap-northeast-2.amazonaws.com/domestic/big/daegu'
WHERE name = '대구';
