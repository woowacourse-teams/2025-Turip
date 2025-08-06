-- Countries (국가)
INSERT INTO country (name)
VALUES ('대한민국'),
       ('일본'),
       ('대만'),
       ('중국');

-- Provinces (도/시)
INSERT INTO province (name)
VALUES ('경기도'),
       ('강원도'),
       ('충청북도'),
       ('충청남도'),
       ('전라북도'),
       ('전라남도'),
       ('경상북도'),
       ('경상남도'),
       ('제주특별자치도');

-- Cities (도시)
-- 대한민국 - 광역시/특별시/자치시
INSERT INTO city (name, country_id, province_id)
VALUES ('서울', 1, null),
       ('부산', 1, null),
       ('대구', 1, null),
       ('인천', 1, null),
       ('광주', 1, null),
       ('대전', 1, null),
       ('울산', 1, null),
       ('세종', 1, null);

-- 대한민국 - 경기도
INSERT INTO city (name, country_id, province_id)
VALUES ('수원', 1, 1),
       ('용인', 1, 1),
       ('성남', 1, 1),
       ('고양', 1, 1),
       ('안산', 1, 1),
       ('안양', 1, 1),
       ('평택', 1, 1),
       ('남양주', 1, 1),
       ('시흥', 1, 1),
       ('의정부', 1, 1),
       ('파주', 1, 1),
       ('김포', 1, 1);

-- 대한민국 - 강원도
INSERT INTO city (name, country_id, province_id)
VALUES ('춘천', 1, 2),
       ('원주', 1, 2),
       ('강릉', 1, 2),
       ('속초', 1, 2);

-- 대한민국 - 충청북도
INSERT INTO city (name, country_id, province_id)
VALUES ('청주', 1, 3),
       ('충주', 1, 3),
       ('제천', 1, 3);

-- 대한민국 - 충청남도
INSERT INTO city (name, country_id, province_id)
VALUES ('천안', 1, 4),
       ('아산', 1, 4),
       ('서산', 1, 4);

-- 대한민국 - 전라북도
INSERT INTO city (name, country_id, province_id)
VALUES ('전주', 1, 5),
       ('익산', 1, 5),
       ('군산', 1, 5);

-- 대한민국 - 전라남도
INSERT INTO city (name, country_id, province_id)
VALUES ('여수', 1, 6),
       ('순천', 1, 6),
       ('목포', 1, 6),
       ('광양', 1, 6),
       ('나주', 1, 6);

-- 대한민국 - 경상북도
INSERT INTO city (name, country_id, province_id)
VALUES ('포항', 1, 7),
       ('경주', 1, 7),
       ('구미', 1, 7),
       ('안동', 1, 7),
       ('상주', 1, 7);

-- 대한민국 - 경상남도
INSERT INTO city (name, country_id, province_id)
VALUES ('창원', 1, 8),
       ('진주', 1, 8),
       ('김해', 1, 8),
       ('양산', 1, 8);

-- 대한민국 - 제주특별자치도
INSERT INTO city (name, country_id, province_id)
VALUES ('제주', 1, 9),
       ('서귀포', 1, 9);

-- 일본
INSERT INTO city (name, country_id, province_id)
VALUES ('도쿄', 2, null),
       ('후쿠오카', 2, null),
       ('교토', 2, null),
       ('오사카', 2, null),
       ('나고야', 2, null),
       ('삿포로', 2, null),
       ('나라', 2, null),
       ('가나자와', 2, null),
       ('하코네', 2, null),
       ('오키나와', 2, null);

-- 대만
INSERT INTO city (name, country_id, province_id)
VALUES ('타이페이', 3, null),
       ('신베이시', 3, null),
       ('타이중', 3, null),
       ('가오슝', 3, null),
       ('타이난', 3, null);

-- 중국
INSERT INTO city (name, country_id, province_id)
VALUES ('베이징', 4, null),
       ('상하이', 4, null),
       ('광저우', 4, null),
       ('선전', 4, null),
       ('청두', 4, null),
       ('시안', 4, null),
       ('충칭', 4, null),
       ('항저우', 4, null),
       ('난징', 4, null),
       ('톈진', 4, null),
       ('우한', 4, null),
       ('리장', 4, null),
       ('장자제', 4, null),
       ('하얼빈', 4, null),
       ('구이린', 4, null),
       ('쿤밍', 4, null),
       ('다리', 4, null);
