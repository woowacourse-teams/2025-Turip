INSERT INTO creator (channel_name, profile_image)
VALUES ('제주에디',
        'https://yt3.ggpht.com/UdF_j2R_Prv7kamTRqItI0QUgMvdbJk_xXFjjYGFUIMZUYt1EbrJpEmzJbciKbrrdoX4me8g5w=s88-c-k-c0x00ffffff-no-rj');

INSERT INTO content (creator_id, region_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '제주에디'),
       (SELECT id FROM city WHERE name = 'sokcho-si'),
       '강원도 4박5일 맛집 여행 코스와 경비 총정리 (속초, 강릉)',
       'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W',
       '2025-2-11';

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('라마다 속초',
        'https://naver.me/G4Wojyef',
        '강원 속초시 대포항희망길 106',
        38.1744296,
        128.6100615);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '라마다 속초' AND address = '강원 속초시 대포항희망길 106'),
        (SELECT id FROM category WHERE name = '숙소'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '라마다 속초' AND address = '강원 속초시 대포항희망길 106'),
        1,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '속초관광수산시장' AND address = '강원 속초시 중앙로147번길 12'),
        1,
        2);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('총각네 대게',
        'https://naver.me/5bVsJfqD',
        '강원 속초시 중앙로147번길 16',
        38.20478516,
        128.5901677);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '총각네 대게' AND address = '강원 속초시 중앙로147번길 16'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '총각네 대게' AND address = '강원 속초시 중앙로147번길 16'),
        1,
        3);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('만석닭강정',
        'https://naver.me/FSw7WhbY',
        '강원 속초시 중앙로147번길 16',
        38.20468677,
        128.5901454);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '만석닭강정' AND address = '강원 속초시 중앙로147번길 16'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '만석닭강정' AND address = '강원 속초시 중앙로147번길 16'),
        1,
        4);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('명천순대',
        'https://naver.me/5EUBZHZn',
        '강원 속초시 아바이마을길 15-6',
        38.20253081,
        128.5935957);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '명천순대' AND address = '강원 속초시 아바이마을길 15-6'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '명천순대' AND address = '강원 속초시 아바이마을길 15-6'),
        2,
        1);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('설악 케이블카',
        'https://naver.me/G58gdO08',
        '강원 속초시 설악산로 1085',
        38.17331063,
        128.4890758);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '설악 케이블카' AND address = '강원 속초시 설악산로 1085'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '설악 케이블카' AND address = '강원 속초시 설악산로 1085'),
        2,
        2);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('아야진 해수욕장',
        'https://naver.me/IFgd8AYY',
        '대한민국 강원특별자치도 고성군 토성면 아야진리 233',
        38.27677904,
        128.5532996);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '아야진 해수욕장' AND address = '대한민국 강원특별자치도 고성군 토성면 아야진리 233'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '아야진 해수욕장' AND address = '대한민국 강원특별자치도 고성군 토성면 아야진리 233'),
        2,
        3);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('영랑호',
        'https://naver.me/5FmSX6x9',
        '강원 속초시 장사동',
        38.21988287,
        128.581238);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '영랑호' AND address = '강원 속초시 장사동'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '영랑호' AND address = '강원 속초시 장사동'),
        2,
        4);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('무진장물회',
        'https://naver.me/xZVNlFMo',
        '강원 속초시 대포항길 17',
        38.17471143,
        128.6051473);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '무진장물회' AND address = '강원 속초시 대포항길 17'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '무진장물회' AND address = '강원 속초시 대포항길 17'),
        3,
        1);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('속초아이 대관람차',
        'https://naver.me/FfeOlf8c',
        '강원 속초시 청호해안길 2',
        38.19093142,
        128.6027817);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '속초아이 대관람차' AND address = '강원 속초시 청호해안길 2'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '속초아이 대관람차' AND address = '강원 속초시 청호해안길 2'),
        3,
        2);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('델피노 더엠브로시아',
        'https://naver.me/xbA5IJDI',
        '강원 고성군 토성면 미시령옛길 1153 소노펠리체 델피노 EAST 10층',
        38.20929355,
        128.4971097);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '델피노 더엠브로시아' AND address = '강원 고성군 토성면 미시령옛길 1153 소노펠리체 델피노 EAST 10층'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '델피노 더엠브로시아' AND address = '강원 고성군 토성면 미시령옛길 1153 소노펠리체 델피노 EAST 10층'),
        3,
        3);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('낙산사',
        'https://naver.me/x5Gojy5X',
        '강원 양양군 강현면 낙산사로 100',
        38.12473373,
        128.6280129);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '낙산사' AND address = '강원 양양군 강현면 낙산사로 100'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '낙산사' AND address = '강원 양양군 강현면 낙산사로 100'),
        3,
        4);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('주문진 해수욕장',
        'https://naver.me/FSw78cAx',
        '강원 강릉시 주문진읍 주문북로 210',
        37.91121663,
        128.8211359);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '주문진 해수욕장' AND address = '강원 강릉시 주문진읍 주문북로 210'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '주문진 해수욕장' AND address = '강원 강릉시 주문진읍 주문북로 210'),
        3,
        5);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('아르떼뮤지엄 강릉',
        'https://naver.me/xjgckgru',
        '강원 강릉시 난설헌로 131',
        37.79190765,
        128.9072007);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '아르떼뮤지엄 강릉' AND address = '강원 강릉시 난설헌로 131'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '아르떼뮤지엄 강릉' AND address = '강원 강릉시 난설헌로 131'),
        3,
        6);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('다모아식당',
        'https://naver.me/xIeMjuhZ',
        '강원 강릉시 해안로 298-5 1층',
        37.7920758,
        128.919512);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '다모아식당' AND address = '강원 강릉시 해안로 298-5 1층'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '다모아식당' AND address = '강원 강릉시 해안로 298-5 1층'),
        3,
        7);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('경포 에메랄드 호텔',
        'https://naver.me/xk1nSZmn',
        '강원 강릉시 해안로406번길 26',
        37.798813,
        128.9147304);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '경포 에메랄드 호텔' AND address = '강원 강릉시 해안로406번길 26'),
        (SELECT id FROM category WHERE name = '숙소'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '경포 에메랄드 호텔' AND address = '강원 강릉시 해안로406번길 26'),
        3,
        8);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('고향산천 초당순두부',
        'https://naver.me/xs3G5UAG',
        '강원 강릉시 난설헌로219번길 34',
        37.79548948,
        128.9130583);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '고향산천 초당순두부' AND address = '강원 강릉시 난설헌로219번길 34'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '고향산천 초당순두부' AND address = '강원 강릉시 난설헌로219번길 34'),
        4,
        1);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('대관령 양떼목장',
        'https://naver.me/xHgIsHx9',
        '강원 평창군 대관령면 대관령마루길 483-32 대관령양떼목장',
        37.68857287,
        128.7527786);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '대관령 양떼목장' AND address = '강원 평창군 대관령면 대관령마루길 483-32 대관령양떼목장'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '대관령 양떼목장' AND address = '강원 평창군 대관령면 대관령마루길 483-32 대관령양떼목장'),
        4,
        2);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('오죽헌',
        'https://naver.me/xf5AFK4A',
        '강원 강릉시 율곡로3139번길 24 오죽헌',
        37.77964635,
        128.8774019);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '오죽헌' AND address = '강원 강릉시 율곡로3139번길 24 오죽헌'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '오죽헌' AND address = '강원 강릉시 율곡로3139번길 24 오죽헌'),
        4,
        3);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('대관령 감자옹심이',
        'https://naver.me/F6lT7VGZ',
        '강원 평창군 대관령면 경강로 5193 대관령감자옹심이',
        37.68017996,
        128.699911);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '대관령 감자옹심이' AND address = '강원 평창군 대관령면 경강로 5193 대관령감자옹심이'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '대관령 감자옹심이' AND address = '강원 평창군 대관령면 경강로 5193 대관령감자옹심이'),
        4,
        4);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('안목해변',
        'https://naver.me/G9r59r5i',
        '강원 강릉시 창해로14번길 20-1',
        37.7747835,
        128.9469287);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '안목해변' AND address = '강원 강릉시 창해로14번길 20-1'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '안목해변' AND address = '강원 강릉시 창해로14번길 20-1'),
        4,
        5);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('스카이베이호텔 경포',
        'https://naver.me/5fIpyHBA',
        '강원 강릉시 해안로 476 스카이베이호텔 경포',
        37.80461182,
        128.9077632);

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '스카이베이호텔 경포' AND address = '강원 강릉시 해안로 476 스카이베이호텔 경포'),
        (SELECT id FROM category WHERE name = '숙소'));

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '스카이베이호텔 경포' AND address = '강원 강릉시 해안로 476 스카이베이호텔 경포'),
        4,
        6);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id FROM content WHERE url = 'https://youtu.be/m2SPPxloo9w?si=6BY2pYrz9FMP2C4W'),
        (SELECT id FROM place WHERE name = '다모아식당' AND address = '강원 강릉시 해안로 298-5 1층'),
        4,
        7);
