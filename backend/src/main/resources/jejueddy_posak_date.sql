-- Content
INSERT INTO content (creator_id, region_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '제주에디'),
       (SELECT id FROM city WHERE name = 'jeju-si'),
       '폭싹 속았수다 제주도 촬영지 모음 (+ 봄 유채꽃 명소까지)',
       'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul',
       '2025-03-28';


-- Place
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('성읍민속마을', 'https://naver.me/GDa2NxZJ', '제주 서귀포시 표선면 성읍리 3294', 33.38589753, 126.8015064);
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('김녕해수욕장', 'https://naver.me/GctrVhXR', '제주 제주시 구좌읍 김녕리', 33.55944641, 126.7608168);
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('관덕정', 'https://naver.me/FuzrBz16', '제주 제주시 관덕로 19 관덕정', 33.51355071, 126.5214998);
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('돌문화공원', 'https://naver.me/xVBxGvRJ', '제주 제주시 조천읍 남조로 2023 교래자연휴양림', 33.44872641, 126.6586904);
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('제주올레길12코스', 'https://naver.me/5HkatkBe', '제주 서귀포시 대정읍 도원중로 48-14 48-30', 33.27342248, 126.2361331);
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('오라동 메밀밭', 'https://naver.me/IMyGTv57', '제주 제주시 오라이동 산76', 33.42701985, 126.5057778);

-- Place_Category
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '성읍민속마을'),
        (SELECT id FROM category WHERE name = '관광지'));
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '김녕해수욕장'),
        (SELECT id FROM category WHERE name = '관광지'));
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '관덕정'),
        (SELECT id FROM category WHERE name = '관광지'));
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '돌문화공원'),
        (SELECT id FROM category WHERE name = '관광지'));
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '제주올레길12코스'),
        (SELECT id FROM category WHERE name = '관광지'));
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '오라동 메밀밭'),
        (SELECT id FROM category WHERE name = '관광지'));

-- Trip Course
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul'),
        (SELECT id FROM place WHERE name = '성읍민속마을' AND address = '제주 서귀포시 표선면 성읍리 3294'),
        1,
        1);
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul'),
        (SELECT id FROM place WHERE name = '김녕해수욕장' AND address = '제주 제주시 구좌읍 김녕리'),
        1,
        2);
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul'),
        (SELECT id FROM place WHERE name = '관덕정' AND address = '제주 제주시 관덕로 19 관덕정'),
        1,
        3);
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul'),
        (SELECT id FROM place WHERE name = '돌문화공원' AND address = '제주 제주시 조천읍 남조로 2023 교래자연휴양림'),
        1,
        4);
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul'),
        (SELECT id FROM place WHERE name = '제주올레길12코스' AND address = '제주 서귀포시 대정읍 도원중로 48-14 48-30'),
        1,
        5);
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/uf4snftpKOk?si=CfgUFgCDBeLulCul'),
        (SELECT id FROM place WHERE name = '오라동 메밀밭' AND address = '제주 제주시 오라이동 산76'),
        1,
        6);
