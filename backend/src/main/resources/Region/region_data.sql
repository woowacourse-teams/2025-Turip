-- Countries (국가)
INSERT INTO country (name, image_url)
VALUES ('대한민국', ''),
       ('일본', 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/overseas/japan.jpeg'),
       ('대만', 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/overseas/taiwan.jpeg'),
       ('중국', 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/overseas/china.jpeg'),
       ('베트남', 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/overseas/vietnam.jpeg');

-- Provinces (도/시)
INSERT INTO province (name)
VALUES ('경기'),
       ('강원'),
       ('충청'),
       ('전라'),
       ('경상'),
       ('제주'),

       ('서울'),
       ('부산'),
       ('대구'),
       ('인천'),
       ('광주'),
       ('대전'),
       ('울산'),
       ('세종');

-- Cities (도시)
-- 대한민국 - 광역시/특별시/자치시
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('서울', 1, 7, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/seoul.jpeg'),
       ('부산', 1, 8, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/busan.jpeg'),
       ('대구', 1, 9, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/daegu.jpeg'),
       ('인천', 1, 10, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/incheon.jpeg'),
       ('광주', 1, 11,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('대전', 1, 12, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/daejeon.jpeg'),
       ('울산', 1, 13,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('세종', 1, 14,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg');

-- 대한민국 - 경기도
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('수원', 1, 1, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/suwon.jpeg'),
       ('용인', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('성남', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('고양', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('안산', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('안양', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('평택', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('남양주', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('시흥', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('의정부', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('파주', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('김포', 1, 1,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg');

-- 대한민국 - 강원
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('춘천', 1, 2,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('원주', 1, 2,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('강릉', 1, 2, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/gangneung.jpeg'),
       ('속초', 1, 2, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/sokcho.jpeg');

-- 대한민국 - 충청
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('청주', 1, 3,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('충주', 1, 3,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('제천', 1, 3,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('천안', 1, 3,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('아산', 1, 3,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('서산', 1, 3,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg');

-- 대한민국 - 전라
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('전주', 1, 4, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/jeonju.jpeg'),
       ('익산', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('군산', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('여수', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('순천', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('목포', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('광양', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('나주', 1, 4,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg');

-- 대한민국 - 경상
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('포항', 1, 5, null),
       ('경주', 1, 5, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/gyeongju.jpeg'),
       ('구미', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('안동', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('상주', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('창원', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('진주', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('김해', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg'),
       ('양산', 1, 5,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg');

-- 대한민국 - 제주
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('제주', 1, 6, 'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/jeju.jpeg'),
       ('서귀포', 1, 6,
        'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/turip/domestic/otherDomestic.jpeg');

-- 일본
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('도쿄', 2, null, null),
       ('후쿠오카', 2, null, null),
       ('교토', 2, null, null),
       ('오사카', 2, null, null),
       ('나고야', 2, null, null),
       ('삿포로', 2, null, null),
       ('나라', 2, null, null),
       ('가나자와', 2, null, null),
       ('하코네', 2, null, null),
       ('오키나와', 2, null, null);

-- 대만
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('타이페이', 3, null, null),
       ('신베이시', 3, null, null),
       ('타이중', 3, null, null),
       ('가오슝', 3, null, null),
       ('타이난', 3, null, null);

-- 중국
INSERT INTO city (name, country_id, province_id, image_url)
VALUES ('베이징', 4, null, null),
       ('상하이', 4, null, null),
       ('광저우', 4, null, null),
       ('선전', 4, null, null),
       ('청두', 4, null, null),
       ('시안', 4, null, null),
       ('충칭', 4, null, null),
       ('항저우', 4, null, null),
       ('난징', 4, null, null),
       ('톈진', 4, null, null),
       ('우한', 4, null, null),
       ('리장', 4, null, null),
       ('장자제', 4, null, null),
       ('하얼빈', 4, null, null),
       ('구이린', 4, null, null),
       ('쿤밍', 4, null, null),
       ('다리', 4, null, null);
