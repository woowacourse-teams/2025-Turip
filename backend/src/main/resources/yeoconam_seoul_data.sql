INSERT INTO content (creator_id, city_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '여코남 - 여행코스짜주는남자'),
       (SELECT id FROM city WHERE name = 'seoul'),
       '[서울투어] 청량리 뚜벅이 여행코스 BEST5 / 청량리역 당일치기 여행 / 안동집 손칼국시 / 금성전파사&스타벅스 경동 1960 / 수호명과 / 플레임 스튜디오 / 산리오 마켓',
       'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar',
       '2023-02-16';

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('안동집 손칼국시', 'https://naver.me/GKUJyXm0', '서울 동대문구 고산자로36길 3 신관 지하1층', 37.57972213, 127.0392882),
       ('금성전파사 새로고침센터', 'https://naver.me/FIf902X6', '서울 동대문구 고산자로36길 3 경동시장 본관 3층, 4층', 37.57991413, 127.0387493),
       ('스타벅스 경동1960점', 'https://naver.me/GlJIDpOC', '서울 동대문구 고산자로36길 3 (제기동)', 37.57979983, 127.0387921),
       ('수호명과', 'https://naver.me/GTnoeLXu', '서울 동대문구 홍릉로 49 1층', 37.58490841, 127.0435901),
       ('플레임 스튜디오', 'https://naver.me/5ZJac2NK', '서울 동대문구 홍릉로 35-1 2층', 37.58366711, 127.0438147),
       ('토이저러스 청량리점', 'https://naver.me/5EUZY1Rj', '서울 동대문구 왕산로 214', 37.58082742, 127.0484184);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '안동집 손칼국시'), (SELECT id FROM category WHERE name = '음식점')),
       ((SELECT id FROM place WHERE name = '금성전파사 새로고침센터'), (SELECT id FROM category WHERE name = '관광지')),
       ((SELECT id FROM place WHERE name = '스타벅스 경동1960점'), (SELECT id FROM category WHERE name = '카페')),
       ((SELECT id FROM place WHERE name = '수호명과'), (SELECT id FROM category WHERE name = '카페')),
       ((SELECT id FROM place WHERE name = '플레임 스튜디오'), (SELECT id FROM category WHERE name = '관광지')),
       ((SELECT id FROM place WHERE name = '토이저러스 청량리점'), (SELECT id FROM category WHERE name = '쇼핑'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar'),
        (SELECT id FROM place WHERE name = '안동집 손칼국시' AND address = '서울 동대문구 고산자로36길 3 신관 지하1층'),
        1,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar'),
        (SELECT id FROM place WHERE name = '금성전파사 새로고침센터' AND address = '서울 동대문구 고산자로36길 3 경동시장 본관 3층, 4층'),
        1,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar'),
        (SELECT id FROM place WHERE name = '스타벅스 경동1960점' AND address = '서울 동대문구 고산자로36길 3 (제기동)'),
        1,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar'),
        (SELECT id FROM place WHERE name = '수호명과' AND address = '서울 동대문구 홍릉로 49 1층'),
        1,
        4);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar'),
        (SELECT id FROM place WHERE name = '플레임 스튜디오' AND address = '서울 동대문구 홍릉로 35-1 2층'),
        1,
        5);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/FTpR_QOqFEQ?si=xsamjUq3ptFqmEar'),
        (SELECT id FROM place WHERE name = '토이저러스 청량리점' AND address = '서울 동대문구 왕산로 214'),
        1,
        6);
