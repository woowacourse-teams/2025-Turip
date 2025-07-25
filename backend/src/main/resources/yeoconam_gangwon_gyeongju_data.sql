INSERT INTO content (creator_id, region_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '여코남 - 여행코스짜주는남자'),
       (SELECT id FROM region WHERE name = 'gyeongsangbuk'),
       '경주여행 1박2일 주요 가이드 총정리 / "아니,여기가 우리가 알던 경주가 맞다고?"',
       'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6',
       '2023-10-7';

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('첨성대',
        'https://naver.me/GJTiv7JT',
        '경북 경주시 인왕동 839-1',
        35.834713,
        129.219047)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('국립경주박물관',
        'https://naver.me/GCvq6Suj',
        '경북 경주시 일정로 186',
        35.829444,
        129.227906)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('황남두꺼비식당',
        'https://naver.me/52R5s0ff',
        '경북 경주시 포석로1050번길 16',
        35.834474,
        129.210925)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('경주동',
        'https://naver.me/GoD7lIEV',
        '경북 경주시 포석로 1089-12 1층',
        35.838438,
        129.209396);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('켄싱턴호텔 설악',
        'https://map.naver.com/p/entry/place/11555552?c=15.00,0,0,0,dh&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507212229&locale=ko&svcName=map_pcv5&businessCategory=hotel&fromPanelNum=1&additionalHeight=76&timestamp=202507212229&locale=ko&svcName=map_pcv5',
        '강원 속초시 설악산로 998',
        38.1731,
        128.498513);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('선화',
        'https://naver.me/FgHNlQNN',
        '경북 경주시 태종로 736-3',
        35.83886,
        129.208902)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('경주대릉원',
        'https://naver.me/x3jFNE36',
        '경북 경주시 황남동 31-1',
        35.838219,
        129.210645)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('월정교',
        'https://naver.me/FG7xnCs8',
        '경북 경주시 교동 274',
        35.829222,
        129.218126)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('천군복합문화공간x에브리블랙',
        'https://naver.me/FDnCJlfb',
        '경북 경주시 경감로 627',
        35.834457,
        129.290717)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('하슬라아트월드',
        'https://naver.me/FgHDJFVH',
        '강원 강릉시 강동면 율곡로 1441',
        37.706184,
        129.011655)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('뮤지엄망고',
        'https://map.naver.com/p/entry/place/1166309201?c=15.00,0,0,0,dh',
        '강원 평창군 대관령면 올림픽로 787',
        37.642732,
        128.67109)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('대관령 삼양라운드힐',
        'https://naver.me/x8tph0jC',
        '강원 평창군 대관령면 꽃밭양지길 708-9',
        37.72619,
        128.719188)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('감자유원지',
        'https://naver.me/x7ndMzi3',
        '강원 강릉시 경강로2115번길 7 1,2,3층',
        37.756349,
        128.897388)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('길손',
        'https://naver.me/5z5sBqgY',
        '강원 강릉시 난설헌로78번길 42-3',
        37.779945,
        128.902204)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('곳',
        'https://naver.me/xs3GYk1H',
        '강원 강릉시 사천면 진리해변길 143',
        37.843091,
        128.873007)
;

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('이진리',
        'https://naver.me/FXwGHU7k',
        '강원 강릉시 임영로 234 카페 이진리',
        37.762058,
        128.892579)
;

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '경주동' AND address = '경북 경주시 포석로 1089-12 1층'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '선화' AND address = '경북 경주시 태종로 736-3'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '경주대릉원' AND address = '경북 경주시 황남동 31-1'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '월정교' AND address = '경북 경주시 교동 274'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '첨성대' AND address = '경북 경주시 인왕동 839-1'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '국립경주박물관' AND address = '경북 경주시 일정로 186'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '천군복합문화공간x에브리블랙' AND address = '경북 경주시 경감로 627'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '천군복합문화공간x에브리블랙' AND address = '경북 경주시 경감로 627'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '황남두꺼비식당' AND address = '경북 경주시 포석로1050번길 16'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '켄싱턴호텔 설악' AND address = '강원 속초시 설악산로 998'),
        (SELECT id FROM category WHERE name = '숙소'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '하슬라아트월드' AND address = '강원 강릉시 강동면 율곡로 1441'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '뮤지엄망고' AND address = '강원 평창군 대관령면 올림픽로 787'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '대관령 삼양라운드힐' AND address = '강원 평창군 대관령면 꽃밭양지길 708-9'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '감자유원지' AND address = '강원 강릉시 경강로2115번길 7 1,2,3층'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '길손' AND address = '강원 강릉시 난설헌로78번길 42-3'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '곳' AND address = '강원 강릉시 사천면 진리해변길 143'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '곳' AND address = '강원 강릉시 사천면 진리해변길 143'),
        (SELECT id FROM category WHERE name = '빵집'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '이진리' AND address = '강원 강릉시 임영로 234 카페 이진리'),
        (SELECT id FROM category WHERE name = '카페'));


INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '경주동' AND address = '경북 경주시 포석로 1089-12 1층'),
        1,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '선화' AND address = '경북 경주시 태종로 736-3'),
        1,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '경주대릉원' AND address = '경북 경주시 황남동 31-1'),
        1,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '월정교' AND address = '경북 경주시 교동 274'),
        1,
        4);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '첨성대' AND address = '경북 경주시 인왕동 839-1'),
        1,
        5);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '국립경주박물관' AND address = '경북 경주시 일정로 186'),
        2,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '천군복합문화공간x에브리블랙' AND address = '경북 경주시 경감로 627'),
        2,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5TF2zJFvXdw&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=6'),
        (SELECT id FROM place WHERE name = '황남두꺼비식당' AND address = '경북 경주시 포석로1050번길 16'),
        2,
        3);

