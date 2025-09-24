-- Countries (국가)
INSERT INTO country (id, name, image_url)
VALUES (1, '대한민국', ''),
       (2, '일본', 'https://drive.google.com/uc?export=view&id=1XJEi-3uY6JSCPbGAHtGPp3DMsX9Fxbre'),
       (3, '대만', 'https://drive.google.com/uc?export=view&id=19ab6qDf2SndV1Pwd8rCMupx8qa_EIO9V'),
       (4, '중국', 'https://drive.google.com/uc?export=view&id=1Wjdvnzp-UEi76VJZNX068vfMfVBopCq3'),
       (5, '베트남', 'https://drive.google.com/uc?export=view&id=12hJnGcO6exvbM-2lZUdSaAx8wAZu864_');

-- Provinces (도/시)
INSERT INTO province (id, name)
VALUES (1, '경기'),
       (2, '강원'),
       (3, '충청'),
       (4, '전라'),
       (5, '경상'),
       (6, '제주'),
       (7, '서울'),
       (8, '부산'),
       (9, '대구'),
       (10, '인천'),
       (11, '광주'),
       (12, '대전'),
       (13, '울산'),
       (14, '세종');

-- Cities (도시)
-- 대한민국 - 광역시/특별시/자치시
INSERT INTO city (id, name, country_id, province_id, image_url)
VALUES (1, '서울', 1, 7, 'https://drive.google.com/uc?export=view&id=1UGj1YCxHUVjQGsHrDmYmQlCuRFkHpn92'),
       (2, '부산', 1, 8, 'https://drive.google.com/uc?export=view&id=1Fbins0H99JXI5nLCRGqTyIAxqRgryliZ'),
       (3, '대구', 1, 9, 'https://drive.google.com/uc?export=view&id=1UL3kAWg19irtiotj-fvzRkOICTt2Fn7k'),
       (4, '인천', 1, 10, 'https://drive.google.com/uc?export=view&id=1hsFHqJKfhvj-GgWQzfDl15OywTgdrOJ5'),
       (5, '광주', 1, 11, ''),
       (6, '대전', 1, 12, 'https://drive.google.com/uc?export=view&id=1MKR-tYsyLcNwoD0V5AHgf-3SSHaDgq_6'),
       (7, '울산', 1, 13, ''),
       (8, '세종', 1, 14, '');

-- (중략: 나머지 도시도 같은 방식으로 INSERT)
-- ...

-- Creator
INSERT INTO creator (id, channel_name, profile_image)
VALUES (1, '연수연',
        'https://yt3.ggpht.com/EMvavcwV96_NkCYm4V8TZIrsytHaiS2AaxS_goqR57WP7kn36qQY92Ujex8JUbBWGQ7P5VY0DA=s88-c-k-c0x00ffffff-no-rj'),
       (2, '듀이ヂュイ♥彡',
        'https://yt3.ggpht.com/qb5S3DAfPxEr1AUR33qY4tfBC6G7VoYFteIQY8bcKxbh4UvShQK6aFVjzUXfkAT1CgS9DDEe=s88-c-k-c0x00ffffff-no-rj'),
       (3, '채리어 Chaerrier',
        'https://yt3.ggpht.com/EK9Xk1iLhR_URYo-uKIVKmcLhENG2JMesba0ErNBYcDVe2x59Nro6JrZESpCg1MzxSRjZSR5=s88-c-k-c0x00ffffff-no-rj'),
       (4, 'Ruby루비',
        'https://yt3.ggpht.com/6LFebJqOGWczxrJavJ05XbkFD-CaJ8Zxuyaozyp9GVY5BYmf9DADJC5urCzIYB4hlghPtRfNPQ=s88-c-k-c0x00ffffff-no-rj'),
       (5, '율모조모',
        'https://yt3.ggpht.com/0yf3QEH-TCREkSAFpl_Q1-6pXGHQVdNMdk6c9Yi16ZHJ_BmBPDfmqu3-g7kHu_V4G6LcXooOJA=s88-c-k-c0x00ffffff-no-rj'),
       (6, '이달래',
        'https://yt3.ggpht.com/iizz44P2jrkT8iY8w24w_6upiKIVsd8QP5HteISzYRpAiMgjG9Ry3fZxVzYtFnXu22TnPtWa=s88-c-k-c0x00ffffff-no-rj'),
       (7, '제주에디 Jeju Eddy',
        'https://yt3.ggpht.com/UdF_j2R_Prv7kamTRqItI0QUgMvdbJk_xXFjjYGFUIMZUYt1EbrJpEmzJbciKbrrdoX4me8g5w=s88-c-k-c0x00ffffff-no-rj'),
       (8, '하찬투어 hachantour',
        'https://yt3.ggpht.com/xMc7FcCl689p_ymaijuY5WOwX9DeHaZ_WTnRHb8UajggQotOO8Bxd0P7cqsYYfubotgjlh4Qfw=s88-c-k-c0x00ffffff-no-rj'),
       (9, '여코남 - 여행코스짜주는남자',
        'https://yt3.ggpht.com/u-Xw8QqIlARFXXN587LDHjc26YRupa53pSXkSbCkLvSDkO_sT8Vl17oPxRAfEuns4etcGlP8=s88-c-k-c0x00ffffff-no-rj');
