INSERT INTO content (creator_id, region_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '여코남 - 여행코스짜주는남자'),
       (SELECT id FROM region WHERE name = 'gyeongsangbuk'),
       '단 5분 만에 강릉 여행에 대한 모든 것 완벽 몰아보기! /혼자,연인,가족 여행 당일치기,1박2일 진짜 다 알려드리는 최종 정리',
       'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2',
       '2024-1-20';

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '하슬라아트월드' AND address = '강원 강릉시 강동면 율곡로 1441'),
        1,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '뮤지엄망고' AND address = '강원 평창군 대관령면 올림픽로 787'),
        1,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '대관령 삼양라운드힐' AND address = '강원 평창군 대관령면 꽃밭양지길 708-9'),
        1,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '감자유원지' AND address = '강원 강릉시 경강로2115번길 7 1,2,3층'),
        1,
        4);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '길손' AND address = '강원 강릉시 난설헌로78번길 42-3'),
        2,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '곳' AND address = '강원 강릉시 사천면 진리해변길 143'),
        2,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE url = 'https://www.youtube.com/watch?v=5EYf3nb8UJ4&list=PL8z7KQfD7gudY2s4CIFJzaxpBnS8uh-AP&index=2'),
        (SELECT id FROM place WHERE name = '이진리' AND address = '강원 강릉시 임영로 234 카페 이진리'),
        2,
        3);
