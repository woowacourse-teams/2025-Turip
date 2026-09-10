-- 아티클(카드뉴스) 더미 데이터
-- Flyway repeatable migration: 이미 article이 존재하면 아무 것도 하지 않는다 (재실행 안전).

-- Place 더미 (article_place 연결용, 이미 동일 name의 place가 있으면 그걸 재사용하고 새로 만들지 않는다)
INSERT INTO place (name, url, address, latitude, longitude)
SELECT * FROM (SELECT '전주 한옥마을' AS name, 'https://place.map.kakao.com/dummy-jeonju-hanok-village' AS url, '전북 전주시 완산구 기린대로 99' AS address, 35.8150 AS latitude, 127.1530 AS longitude) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM place WHERE name = '전주 한옥마을');

INSERT INTO place (name, url, address, latitude, longitude)
SELECT * FROM (SELECT '전동성당' AS name, 'https://place.map.kakao.com/dummy-jeondong-cathedral' AS url, '전북 전주시 완산구 태조로 51' AS address, 35.8125 AS latitude, 127.1500 AS longitude) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM place WHERE name = '전동성당');

INSERT INTO place (name, url, address, latitude, longitude)
SELECT * FROM (SELECT '경기전' AS name, 'https://place.map.kakao.com/dummy-gyeonggijeon' AS url, '전북 전주시 완산구 태조로 44' AS address, 35.8144 AS latitude, 127.1516 AS longitude) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM place WHERE name = '경기전');

INSERT INTO place (name, url, address, latitude, longitude)
SELECT * FROM (SELECT '전주 남부시장 청년몰' AS name, 'https://place.map.kakao.com/dummy-nambu-market' AS url, '전북 전주시 완산구 풍남문2길 39' AS address, 35.8113 AS latitude, 127.1466 AS longitude) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM place WHERE name = '전주 남부시장 청년몰');

-- Tag 더미 (이미 동일 name의 tag가 있으면 재사용)
INSERT INTO tag (name)
SELECT * FROM (SELECT '전주' AS name) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tag WHERE name = '전주');

INSERT INTO tag (name)
SELECT * FROM (SELECT '한옥마을' AS name) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tag WHERE name = '한옥마을');

INSERT INTO tag (name)
SELECT * FROM (SELECT '맛집' AS name) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tag WHERE name = '맛집');

INSERT INTO tag (name)
SELECT * FROM (SELECT '카페' AS name) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tag WHERE name = '카페');

INSERT INTO tag (name)
SELECT * FROM (SELECT '드라이브' AS name) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tag WHERE name = '드라이브');

-- Article 더미 5개 (author는 role='ADMIN'인 계정 중 하나를 동적으로 선택, ADMIN이 없으면 자연히 NULL)
-- 각 article은 title로 존재 여부를 체크해 개별적으로 건너뛴다 (재실행 안전, 기존 article과 공존 가능).
-- display_order는 기존 article과 충돌하지 않도록 현재 최대값 다음으로 이어붙인다.
INSERT INTO article (title, subtitle, content, thumbnail_url, author_id, display_order, is_published, created_at, updated_at)
SELECT * FROM (
    SELECT
        '전주 한옥마을 완전 정복 코스' AS title,
        '반나절이면 충분한 한옥마을 핵심 동선' AS subtitle,
        '# 전주 한옥마을 완전 정복 코스\n\n전주 여행의 시작은 언제나 한옥마을입니다.\n\n![한옥마을 전경](https://picsum.photos/seed/turip-article-1-1/800/600)\n\n좁은 골목 사이사이 카페와 공방이 숨어 있어 걷는 재미가 있습니다.\n\n![골목 카페 거리](https://picsum.photos/seed/turip-article-1-2/800/600)\n\n**추천 동선**: 경기전 → 전동성당 → 골목 카페 순으로 돌면 동선 낭비가 없습니다.' AS content,
        'https://picsum.photos/seed/turip-article-1-1/800/600' AS thumbnail_url,
        (SELECT id FROM account WHERE role = 'ADMIN' ORDER BY id LIMIT 1) AS author_id,
        (SELECT COALESCE(MAX(display_order), -1) + 1 FROM article) AS display_order,
        TRUE AS is_published,
        NOW() AS created_at,
        NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM article WHERE title = '전주 한옥마을 완전 정복 코스');

INSERT INTO article (title, subtitle, content, thumbnail_url, author_id, display_order, is_published, created_at, updated_at)
SELECT * FROM (
    SELECT
        '전동성당 앞에서 찍는 인생샷' AS title,
        '해질녘 노을과 어우러지는 붉은 벽돌' AS subtitle,
        '# 전동성당 앞에서 찍는 인생샷\n\n전동성당은 전주에서 손꼽히는 사진 명소입니다.\n\n![전동성당 외관](https://picsum.photos/seed/turip-article-2-1/800/600)\n\n특히 해질녘에는 노을과 붉은 벽돌이 어우러져 색다른 분위기를 냅니다.\n\n![해질녘 성당 풍경](https://picsum.photos/seed/turip-article-2-2/800/600)\n\n주말 오후 4시 이후 방문을 추천합니다.' AS content,
        'https://picsum.photos/seed/turip-article-2-1/800/600' AS thumbnail_url,
        (SELECT id FROM account WHERE role = 'ADMIN' ORDER BY id LIMIT 1) AS author_id,
        (SELECT COALESCE(MAX(display_order), -1) + 1 FROM article) AS display_order,
        TRUE AS is_published,
        NOW() AS created_at,
        NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM article WHERE title = '전동성당 앞에서 찍는 인생샷');

INSERT INTO article (title, subtitle, content, thumbnail_url, author_id, display_order, is_published, created_at, updated_at)
SELECT * FROM (
    SELECT
        '경기전에서 즐기는 한복 나들이' AS title,
        '조선 왕조의 흔적을 한복 입고 걷기' AS subtitle,
        '# 경기전에서 즐기는 한복 나들이\n\n경기전은 태조 이성계의 어진을 모신 곳으로, 고즈넉한 분위기가 일품입니다.\n\n![경기전 입구](https://picsum.photos/seed/turip-article-3-1/800/600)\n\n한옥마을 인근 한복 대여점에서 한복을 빌려 입으면 더욱 특별한 사진을 남길 수 있습니다.\n\n![한복 입은 관광객](https://picsum.photos/seed/turip-article-3-2/800/600)\n\n![경기전 대나무숲](https://picsum.photos/seed/turip-article-3-3/800/600)' AS content,
        'https://picsum.photos/seed/turip-article-3-1/800/600' AS thumbnail_url,
        (SELECT id FROM account WHERE role = 'ADMIN' ORDER BY id LIMIT 1) AS author_id,
        (SELECT COALESCE(MAX(display_order), -1) + 1 FROM article) AS display_order,
        TRUE AS is_published,
        NOW() AS created_at,
        NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM article WHERE title = '경기전에서 즐기는 한복 나들이');

INSERT INTO article (title, subtitle, content, thumbnail_url, author_id, display_order, is_published, created_at, updated_at)
SELECT * FROM (
    SELECT
        '남부시장 청년몰 야시장 먹킷리스트' AS title,
        '금요일 저녁이면 열리는 전주의 밤시장' AS subtitle,
        '# 남부시장 청년몰 야시장 먹킷리스트\n\n매주 금요일·토요일 저녁, 남부시장 청년몰 일대에 야시장이 열립니다.\n\n![야시장 전경](https://picsum.photos/seed/turip-article-4-1/800/600)\n\n다양한 청년 상인들의 이색 먹거리를 만나볼 수 있어요.\n\n![야시장 먹거리](https://picsum.photos/seed/turip-article-4-2/800/600)\n\n**추천 메뉴**: 수제 맥주, 퓨전 전, 흑임자 라떼' AS content,
        'https://picsum.photos/seed/turip-article-4-1/800/600' AS thumbnail_url,
        (SELECT id FROM account WHERE role = 'ADMIN' ORDER BY id LIMIT 1) AS author_id,
        (SELECT COALESCE(MAX(display_order), -1) + 1 FROM article) AS display_order,
        TRUE AS is_published,
        NOW() AS created_at,
        NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM article WHERE title = '남부시장 청년몰 야시장 먹킷리스트');

INSERT INTO article (title, subtitle, content, thumbnail_url, author_id, display_order, is_published, created_at, updated_at)
SELECT * FROM (
    SELECT
        '(비공개 초안) 전주 근교 드라이브 코스' AS title,
        '아직 다듬는 중인 초안입니다' AS subtitle,
        '# 전주 근교 드라이브 코스 (초안)\n\n아직 작성 중인 비공개 아티클입니다.\n\n![드라이브 코스 예시](https://picsum.photos/seed/turip-article-5-1/800/600)\n\n완성되면 공개로 전환될 예정입니다.' AS content,
        'https://picsum.photos/seed/turip-article-5-1/800/600' AS thumbnail_url,
        (SELECT id FROM account WHERE role = 'ADMIN' ORDER BY id LIMIT 1) AS author_id,
        (SELECT COALESCE(MAX(display_order), -1) + 1 FROM article) AS display_order,
        FALSE AS is_published,
        NOW() AS created_at,
        NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM article WHERE title = '(비공개 초안) 전주 근교 드라이브 코스');

-- ArticleTag 연결 (title로 article을 찾아 2개씩 태그 연결)
INSERT INTO article_tag (article_id, tag_id)
SELECT a.id, t.id FROM article a, tag t
WHERE a.title = '전주 한옥마을 완전 정복 코스' AND t.name IN ('전주', '한옥마을')
  AND NOT EXISTS (SELECT 1 FROM article_tag WHERE article_id = a.id AND tag_id = t.id);

INSERT INTO article_tag (article_id, tag_id)
SELECT a.id, t.id FROM article a, tag t
WHERE a.title = '전동성당 앞에서 찍는 인생샷' AND t.name IN ('전주', '카페')
  AND NOT EXISTS (SELECT 1 FROM article_tag WHERE article_id = a.id AND tag_id = t.id);

INSERT INTO article_tag (article_id, tag_id)
SELECT a.id, t.id FROM article a, tag t
WHERE a.title = '경기전에서 즐기는 한복 나들이' AND t.name IN ('전주', '한옥마을')
  AND NOT EXISTS (SELECT 1 FROM article_tag WHERE article_id = a.id AND tag_id = t.id);

INSERT INTO article_tag (article_id, tag_id)
SELECT a.id, t.id FROM article a, tag t
WHERE a.title = '남부시장 청년몰 야시장 먹킷리스트' AND t.name IN ('전주', '맛집')
  AND NOT EXISTS (SELECT 1 FROM article_tag WHERE article_id = a.id AND tag_id = t.id);

INSERT INTO article_tag (article_id, tag_id)
SELECT a.id, t.id FROM article a, tag t
WHERE a.title = '(비공개 초안) 전주 근교 드라이브 코스' AND t.name IN ('전주', '드라이브')
  AND NOT EXISTS (SELECT 1 FROM article_tag WHERE article_id = a.id AND tag_id = t.id);

-- ArticlePlace 연결 (title/name으로 조회해 연결, place가 없으면 자연히 스킵됨)
INSERT INTO article_place (article_id, place_id)
SELECT a.id, p.id FROM article a, place p
WHERE a.title = '전주 한옥마을 완전 정복 코스' AND p.name = '전주 한옥마을'
  AND NOT EXISTS (SELECT 1 FROM article_place WHERE article_id = a.id AND place_id = p.id);

INSERT INTO article_place (article_id, place_id)
SELECT a.id, p.id FROM article a, place p
WHERE a.title = '전동성당 앞에서 찍는 인생샷' AND p.name = '전동성당'
  AND NOT EXISTS (SELECT 1 FROM article_place WHERE article_id = a.id AND place_id = p.id);

INSERT INTO article_place (article_id, place_id)
SELECT a.id, p.id FROM article a, place p
WHERE a.title = '경기전에서 즐기는 한복 나들이' AND p.name = '경기전'
  AND NOT EXISTS (SELECT 1 FROM article_place WHERE article_id = a.id AND place_id = p.id);

INSERT INTO article_place (article_id, place_id)
SELECT a.id, p.id FROM article a, place p
WHERE a.title = '남부시장 청년몰 야시장 먹킷리스트' AND p.name = '전주 남부시장 청년몰'
  AND NOT EXISTS (SELECT 1 FROM article_place WHERE article_id = a.id AND place_id = p.id);
