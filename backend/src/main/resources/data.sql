ALTER TABLE place ALTER COLUMN url VARCHAR(65535);

-- Region
INSERT INTO region (name)
VALUES ('seoul');
INSERT INTO region (name)
VALUES ('busan');
INSERT INTO region (name)
VALUES ('daegu');
INSERT INTO region (name)
VALUES ('incheon');
INSERT INTO region (name)
VALUES ('gwangju');
INSERT INTO region (name)
VALUES ('daejeon');
INSERT INTO region (name)
VALUES ('ulsan');
INSERT INTO region (name)
VALUES ('sejong');
INSERT INTO region (name)
VALUES ('gyeonggi');
INSERT INTO region (name)
VALUES ('gangwon');
INSERT INTO region (name)
VALUES ('chungcheongbuk');
INSERT INTO region (name)
VALUES ('chungcheongnam');
INSERT INTO region (name)
VALUES ('jeollabuk');
INSERT INTO region (name)
VALUES ('jeollanam');
INSERT INTO region (name)
VALUES ('gyeongsangbuk');
INSERT INTO region (name)
VALUES ('gyeongsangnam');
INSERT INTO region (name)
VALUES ('jeju');

-- Creator
INSERT INTO category (name)
VALUES ('음식점'),
       ('술집'),
       ('빵집'),
       ('숙소'),
       ('관광지'),
       ('소품샵'),
       ('쇼핑'),
       ('시장'),
       ('카페');

-- Content
INSERT INTO creator (channel_name, profile_image)
VALUES ('연수연',
        'https://yt3.googleusercontent.com/EMvavcwV96_NkCYm4V8TZIrsytHaiS2AaxS_goqR57WP7kn36qQY92Ujex8JUbBWGQ7P5VY0DA=s160-c-k-c0x00ffffff-no-rj');

-- Category
INSERT INTO content (creator_id, region_id, title, url, uploaded_date)
SELECT (SELECT id FROM creator WHERE channel_name = '연수연'),
       (SELECT id FROM region WHERE name = 'busan'),
       '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그',
       'https://www.youtube.com/watch?v=U7vwpgZlD6Q',
       '2025-07-01';

-- Place
INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('거북이금고',
        'https://map.naver.com/p/search/거북이금고/place/38623885',
        '부산 해운대구 중동1로 32 지상1층',
        35.162851,
        129.162647);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('해운대',
        'https://map.naver.com/p/search/%ED%95%B4%EC%9A%B4%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/11491806?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230900&locale=ko&svcName=map_pcv5&searchText=%ED%95%B4%EC%9A%B4%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5',
        '부산 해운대구 해운대해변로 264',
        35.160936,
        129.16004);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('상국이네',
        'https://map.naver.com/p/search/%ED%95%B4%EC%9A%B4%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/11491806?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230900&locale=ko&svcName=map_pcv5&searchText=%ED%95%B4%EC%9A%B4%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5',
        '부산 해운대구 해운대해변로 264',
        35.160936,
        129.16004);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('고래서이뻐 해리단길',
        'https://map.naver.com/p/search/%EA%B3%A0%EB%9E%98%EC%84%9C%20%EC%9D%B4%EB%BB%90%20%ED%95%B4%EB%A6%AC%EB%8B%A8%EA%B8%B8/place/1335171220?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230904&locale=ko&svcName=map_pcv5&searchText=%EA%B3%A0%EB%9E%98%EC%84%9C%20%EC%9D%B4%EB%BB%90%20%ED%95%B4%EB%A6%AC%EB%8B%A8%EA%B8%B8',
        '부산 해운대구 우동 519-10',
        35.166802,
        129.157888);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('어컵오브웜티',
        'https://map.naver.com/p/search/%EC%96%B4%EC%BB%B5%EC%98%A4%EB%B8%8C%EC%9B%9C%ED%8B%B0/place/1705166806?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230907&locale=ko&svcName=map_pcv5&searchText=%EC%96%B4%EC%BB%B5%EC%98%A4%EB%B8%8C%EC%9B%9C%ED%8B%B0',
        '부산 해운대구 우동 518',
        35.166458,
        129.157896);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('코오리마찌 해운대해리단길',
        'https://map.naver.com/p/entry/place/1843212662?c=15.00,0,0,0,dh&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230909&locale=ko&svcName=map_pcv5',
        '부산 해운대구 우동 449-3',
        35.166407,
        129.158467);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('브로쓰마카롱 해운대점',
        'https://map.naver.com/p/search/%EB%B8%8C%EB%A1%9C%EC%93%B0%EB%A7%88%EC%B9%B4%EB%A1%B1/place/1682715292?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230911&locale=ko&svcName=map_pcv5&searchText=%EB%B8%8C%EB%A1%9C%EC%93%B0%EB%A7%88%EC%B9%B4%EB%A1%B1',
        '부산 해운대구 우동 563-4',
        35.175183,
        129.162343);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('신세계백화점 센텀시티점',
        'https://map.naver.com/p/search/%EC%8B%A0%EC%84%B8%EA%B3%84%EB%B0%B1%ED%99%94%EC%A0%90/place/13067134?c=11.00,0,0,0,dh&placePath=/home?from=map&fromPanelNum=2&timestamp=202507230912&locale=ko&svcName=map_pcv5&searchText=%EC%8B%A0%EC%84%B8%EA%B3%84%EB%B0%B1%ED%99%94%EC%A0%90',
        '부산 해운대구 센텀남대로 35',
        35.169588,
        129.129545);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('개미집 광안리본점',
        'https://map.naver.com/p/search/%EA%B0%9C%EB%AF%B8%EC%A7%91%20%EA%B4%91%EC%95%88%EB%A6%AC%EB%B3%B8%EC%A0%90/place/20418703?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230914&locale=ko&svcName=map_pcv5&searchText=%EA%B0%9C%EB%AF%B8%EC%A7%91%20%EA%B4%91%EC%95%88%EB%A6%AC%EB%B3%B8%EC%A0%90',
        '부산 수영구 광안동 194-7',
        35.153846,
        129.11714);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('밀락더마켓',
        'https://map.naver.com/p/search/%EB%B0%80%EB%9D%BD%EB%8D%94%EB%A7%88%EC%BC%93/place/1192293380?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230915&locale=ko&svcName=map_pcv5&searchText=%EB%B0%80%EB%9D%BD%EB%8D%94%EB%A7%88%EC%BC%93',
        '부산 수영구 민락동 113-31',
        35.154648,
        129.126649);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('호텔1',
        'https://map.naver.com/p/search/%ED%98%B8%ED%85%941/place/897879759?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230917&locale=ko&svcName=map_pcv5&searchText=%ED%98%B8%ED%85%941&businessCategory=hotel&from=map&fromPanelNum=2&timestamp=202507230917&locale=ko&svcName=map_pcv5&searchText=%ED%98%B8%ED%85%941',
        '부산 수영구 광안동 196-3',
        35.153169,
        129.117304);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('일등가마솥돼지국밥 남포점',
        'https://map.naver.com/p/search/%EC%9D%BC%EB%93%B1%EA%B0%80%EB%A7%88%EC%86%A5%EB%8F%BC%EC%A7%80%EA%B5%AD%EB%B0%A5%20%EB%82%A8%ED%8F%AC%EC%A0%90/place/718740707?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230919&locale=ko&svcName=map_pcv5&searchText=%EC%9D%BC%EB%93%B1%EA%B0%80%EB%A7%88%EC%86%A5%EB%8F%BC%EC%A7%80%EA%B5%AD%EB%B0%A5%20%EB%82%A8%ED%8F%AC%EC%A0%90',
        '부산 중구 비프광장로 5',
        35.100032,
        129.025891);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('부평깡통시장',
        'https://map.naver.com/p/search/%EB%B6%80%ED%8F%89%EA%B9%A1%ED%86%B5%EC%8B%9C%EC%9E%A5/place/13346401?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230921&locale=ko&svcName=map_pcv5&searchText=%EB%B6%80%ED%8F%89%EA%B9%A1%ED%86%B5%EC%8B%9C%EC%9E%A5/place/718740707?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230919&locale=ko&svcName=map_pcv5&searchText=%EC%9D%BC%EB%93%B1%EA%B0%80%EB%A7%88%EC%86%A5%EB%8F%BC%EC%A7%80%EA%B5%AD%EB%B0%A5%20%EB%82%A8%ED%8F%AC%EC%A0%90',
        '부산 중구 부평1길 48',
        35.102345,
        129.025614);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('보수동책방골목',
        'https://map.naver.com/p/search/%EB%B3%B4%EC%88%98%EB%8F%99%EC%B1%85%EB%B0%A9%EA%B3%A8%EB%AA%A9/place/12848261?c=16.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230922&locale=ko&svcName=map_pcv5&searchText=%EB%B3%B4%EC%88%98%EB%8F%99%EC%B1%85%EB%B0%A9%EA%B3%A8%EB%AA%A9',
        '부산 중구 대청로 67-1',
        35.104069,
        129.027433);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('해빙모먼트',
        'https://map.naver.com/p/search/%ED%95%B4%EB%B9%99%EB%AA%A8%EB%A8%BC%ED%8A%B8/place/1549047717?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230923&locale=ko&svcName=map_pcv5&searchText=%ED%95%B4%EB%B9%99%EB%AA%A8%EB%A8%BC%ED%8A%B8',
        '부산 영도구 영선동4가 612',
        35.08082,
        129.043748);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('흰여울문화마을',
        'https://map.naver.com/p/search/%ED%9D%B0%EC%97%AC%EC%9A%B8%EB%AC%B8%ED%99%94%EB%A7%88%EC%9D%84/place/37418047?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230925&locale=ko&svcName=map_pcv5&searchText=%ED%9D%B0%EC%97%AC%EC%9A%B8%EB%AC%B8%ED%99%94%EB%A7%88%EC%9D%84',
        '부산 영도구 영선동4가 605-3',
        35.079087,
        129.045148);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('기장손칼국수',
        'https://map.naver.com/p/search/%EA%B8%B0%EC%9E%A5%EC%86%90%EC%B9%BC%EA%B5%AD%EC%88%98/place/20601429?c=10.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230926&locale=ko&svcName=map_pcv5&searchText=%EA%B8%B0%EC%9E%A5%EC%86%90%EC%B9%BC%EA%B5%AD%EC%88%98',
        '부산 부산진구 서면로 56',
        35.155632,
        129.058348);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('얼룩',
        'https://map.naver.com/p/search/%EC%96%BC%EB%A3%A9/place/1887684443?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230928&locale=ko&svcName=map_pcv5&searchText=%EC%96%BC%EB%A3%A9',
        '부산 부산진구 전포동 334-9',
        35.079087,
        129.067962);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('이재모피자 서면2호점',
        'https://map.naver.com/p/search/%EC%9D%B4%EC%9E%AC%EB%AA%A8%ED%94%BC%EC%9E%90%20%EC%84%9C%EB%A9%B42%ED%98%B8%EC%A0%90/place/1151421935?c=15.00,0,0,0,dh&isCorrectAnswer=true&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230929&locale=ko&svcName=map_pcv5&searchText=%EC%9D%B4%EC%9E%AC%EB%AA%A8%ED%94%BC%EC%9E%90%20%EC%84%9C%EB%A9%B42%ED%98%B8%EC%A0%90',
        '부산 부산진구 전포동 688-2',
        35.155729,
        129.064845);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('CRR 전포',
        'https://map.naver.com/p/entry/place/1064121174?c=15.00,0,0,0,dh&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230931&locale=ko&svcName=map_pcv5',
        '부산 부산진구 전포동 677-11',
        35.156258,
        129.064458);

INSERT INTO place (name, url, address, latitude, longitude)
VALUES ('김유순대구뽈찜',
        'https://map.naver.com/p/entry/place/11711165?c=15.00,0,0,0,dh&placePath=/home?from=map&fromPanelNum=1&additionalHeight=76&timestamp=202507230932&locale=ko&svcName=map_pcv5',
        '부산 남구 진남로 15',
        35.137142,
        129.09127);

-- PlaceCategory
INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '거북이금고'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '해운대'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '상국이네'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '고래서이뻐 해리단길'),
        (SELECT id FROM category WHERE name = '소품샵'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '어컵오브웜티'),
        (SELECT id FROM category WHERE name = '소품샵'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '코오리마찌 해운대해리단길'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '브로쓰마카롱 해운대점'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '신세계백화점 센텀시티점'),
        (SELECT id FROM category WHERE name = '쇼핑'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '개미집 광안리본점'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '밀락더마켓'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '호텔1'),
        (SELECT id FROM category WHERE name = '숙소'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '일등가마솥돼지국밥 남포점'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '부평깡통시장'),
        (SELECT id FROM category WHERE name = '시장'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '보수동책방골목'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '해빙모먼트'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '흰여울문화마을'),
        (SELECT id FROM category WHERE name = '관광지'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '기장손칼국수'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '얼룩'),
        (SELECT id FROM category WHERE name = '카페'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '이재모피자 서면2호점'),
        (SELECT id FROM category WHERE name = '음식점'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = 'CRR 전포'),
        (SELECT id FROM category WHERE name = '쇼핑'));

INSERT INTO place_category (place_id, category_id)
VALUES ((SELECT id FROM place WHERE name = '김유순대구뽈찜'),
        (SELECT id FROM category WHERE name = '음식점'));

-- TripCourse
INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '거북이금고'),
        1,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '해운대'),
        1,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '상국이네'),
        1,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '고래서이뻐 해리단길'),
        1,
        4);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '어컵오브웜티'),
        1,
        5);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '코오리마찌 해운대해리단길'),
        1,
        6);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '브로쓰마카롱 해운대점'),
        1,
        7);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '신세계백화점 센텀시티점'),
        1,
        8);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '개미집 광안리본점'),
        1,
        9);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '밀락더마켓'),
        1,
        10);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '호텔1'),
        1,
        11);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '일등가마솥돼지국밥 남포점'),
        2,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '부평깡통시장'),
        2,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '보수동책방골목'),
        2,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '해빙모먼트'),
        2,
        4);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '흰여울문화마을'),
        2,
        5);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '기장손칼국수'),
        2,
        6);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '얼룩'),
        3,
        1);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '이재모피자 서면2호점'),
        3,
        2);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = 'CRR 전포'),
        3,
        3);

INSERT INTO trip_course (content_id, place_id, visit_day, visit_order)
VALUES ((SELECT id
         FROM content
         WHERE title =
               '나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그'),
        (SELECT id FROM place WHERE name = '김유순대구뽈찜'),
        3,
        4);
