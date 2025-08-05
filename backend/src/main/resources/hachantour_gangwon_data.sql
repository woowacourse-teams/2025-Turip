INSERT INTO content (creator_id, city_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '하찬투어 hachantour'),
       (SELECT id FROM city WHERE name = '속초'),
       '외국인 많음주의 국내여행중 만난 유럽감성 해외보다 더 멋진 강원도 속초 호텔 추천 유명맛집과 가볼만한 곳 직접 다녀와봤습니다 솔직 후기',
       'https://www.youtube.com/watch?v=G6BaTbbj2hA',
       '2025-4-22';

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('오봉식당',
        'https://map.naver.com/p/search/오봉식당/place/12868910?...',
        '강원 속초시 중앙로 398',
        38.223645,
        128.588942);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('청초수물회 속초본점',
        'https://map.naver.com/p/search/청초수물회/place/12028621?...',
        '강원 속초시 엑스포로 12-36 청초수물회 속초본점',
        38.192355,
        128.590454);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('설악케이블카',
        'https://map.naver.com/p/search/설악산/place/1244805381?...',
        '강원 속초시 설악산로 1085',
        38.173277,
        128.489065);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('영금정',
        'https://map.naver.com/p/search/영금정/place/15085338?...',
        '강원 속초시 영금정로 43',
        38.212277,
        128.602082);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('속초해수욕장',
        'https://map.naver.com/p/search/속초해수욕장/place/11491735?...',
        '강원 속초시 조양동',
        38.191593,
        128.603584);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('속초관광수산시장',
        'https://map.naver.com/p/search/속초 중앙시장/place/13345965?...',
        '강원 속초시 중앙로147번길 12',
        38.204687,
        128.590235);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '오봉식당' AND address = '강원 속초시 중앙로 398'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '청초수물회 속초본점' AND address = '강원 속초시 엑스포로 12-36 청초수물회 속초본점'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '설악케이블카' AND address = '강원 속초시 설악산로 1085'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '영금정' AND address = '강원 속초시 영금정로 43'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '속초해수욕장' AND address = '강원 속초시 조양동'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '속초관광수산시장' AND address = '강원 속초시 중앙로147번길 12'),
        (SELECT id FROM category WHERE name = '시장'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '오봉식당' AND address = '강원 속초시 중앙로 398'),
        1,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '켄싱턴호텔 설악' AND address = '강원 속초시 설악산로 998'),
        1,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '청초수물회 속초본점' AND address = '강원 속초시 엑스포로 12-36 청초수물회 속초본점'),
        1,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '설악케이블카' AND address = '강원 속초시 설악산로 1085'),
        2,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '영금정' AND address = '강원 속초시 영금정로 43'),
        2,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '속초해수욕장' AND address = '강원 속초시 조양동'),
        2,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://www.youtube.com/watch?v=G6BaTbbj2hA'),
        (SELECT id FROM place WHERE name = '속초관광수산시장' AND address = '강원 속초시 중앙로147번길 12'),
        2,
        4);
