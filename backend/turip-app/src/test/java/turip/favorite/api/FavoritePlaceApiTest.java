package turip.favorite.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import turip.favorite.domain.AccountRole;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FavoritePlaceApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_folder_account");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM guest");
        jdbcTemplate.update("DELETE FROM refresh_token");
        jdbcTemplate.update("DELETE FROM social_member");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM account");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE guest ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE social_member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE refresh_token ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE account ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder_account ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/api/v1/turips/places POST 장소 찜 생성 테스트")
    @Nested
    class Create {

        @DisplayName("장소 찜 생성에 성공한 경우 201 CREATED 코드와 생성된 장소 찜 정보를 응답한다")
        @Test
        void create1() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(201);
        }

        @DisplayName("첫 번째 장소 찜 생성 시 favoriteOrder이 1로 설정된다")
        @Test
        void create2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('첫 번째 장소','https://naver.me/place1', '첫 번째 장소 주소', 37.1234, 127.1234)");

            // when
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(201);

            // then - favoriteOrder이 1로 설정되었는지 확인
            Integer favoriteOrder = jdbcTemplate.queryForObject(
                    "SELECT favorite_order FROM favorite_place WHERE favorite_folder_id = 1 AND place_id = 1",
                    Integer.class);
            assertThat(favoriteOrder).isEqualTo(1);
        }

        @DisplayName("두 번째 장소 찜 생성 시 favoriteOrder이 2로 설정된다")
        @Test
        void create3() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('첫 번째 장소','https://naver.me/place1', '첫 번째 장소 주소', 37.1234, 127.1234)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('두 번째 장소','https://naver.me/place2', '두 번째 장소 주소', 37.5678, 127.5678)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 1, 1)");

            // when
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 2L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(201);

            // then - favoriteOrder이 2로 설정되었는지 확인
            Integer favoriteOrder = jdbcTemplate.queryForObject(
                    "SELECT favorite_order FROM favorite_place WHERE favorite_folder_id = 1 AND place_id = 2",
                    Integer.class);
            assertThat(favoriteOrder).isEqualTo(2);
        }

        @DisplayName("여러 장소 찜 생성 시 favoriteOrder이 순차적으로 증가한다")
        @Test
        void create4() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소1','https://naver.me/place1', '장소1 주소', 37.1234, 127.1234)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소2','https://naver.me/place2', '장소2 주소', 37.5678, 127.5678)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소3','https://naver.me/place3', '장소3 주소', 37.9999, 127.9999)");

            // when - 3개의 장소를 순차적으로 찜
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(201);

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 2L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(201);

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 3L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(201);

            // then - favoriteOrder이 1, 2, 3으로 순차적으로 설정되었는지 확인
            Integer favoriteOrder1 = jdbcTemplate.queryForObject(
                    "SELECT favorite_order FROM favorite_place WHERE favorite_folder_id = 1 AND place_id = 1",
                    Integer.class);
            Integer favoriteOrder2 = jdbcTemplate.queryForObject(
                    "SELECT favorite_order FROM favorite_place WHERE favorite_folder_id = 1 AND place_id = 2",
                    Integer.class);
            Integer favoriteOrder3 = jdbcTemplate.queryForObject(
                    "SELECT favorite_order FROM favorite_place WHERE favorite_folder_id = 1 AND place_id = 3",
                    Integer.class);

            assertThat(favoriteOrder1).isEqualTo(1);
            assertThat(favoriteOrder2).isEqualTo(2);
            assertThat(favoriteOrder3).isEqualTo(3);
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 403 FORBIDDEN을 응답한다")
        @Test
        void create5() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'may')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "cool")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void create6() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void create7() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("이미 해당 폴더에 찜한 장소인 경우 409 CONFLICT를 응답한다")
        @Test
        void create8() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips/places")
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("/api/v1/turips/places GET 장소 찜 폴더의 장소 찜 목록 조회 테스트")
    @Nested
    class ReadAllByFolder {

        @DisplayName("조회에 성공한 경우 200 OK 코드와 장소 찜 목록을 응답한다")
        @Test
        void readAllByFolder1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('스타벅스','https://naver.me/abc123', '스타벅스의 도로명 주소', 37.5678, 126.9876)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 2, 2)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("turipId", 1L)
                    .contentType(ContentType.JSON)
                    .when().get("/api/v1/turips/places")
                    .then()
                    .statusCode(200)
                    .body("turipPlaceCount", is(2))
                    .body("turipPlaces[0].place.name", is("루터회관"))
                    .body("turipPlaces[1].place.name", is("스타벅스"));
        }

        @DisplayName("장소가 favoriteOrder 오름차순으로 정렬되어 반환된다")
        @Test
        void readAllByFolder2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            for (int i = 1; i <= 5; i++) {
                jdbcTemplate.update(
                        "INSERT INTO place (name, url, address, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                        "장소" + i, "https://naver.me/place" + i, "장소" + i + " 주소", 37.0 + i, 127.0 + i);
            }

            // favoriteOrder을 5, 1, 3, 2, 4 순서로 삽입 (정렬 후 1,2,3,4,5)
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 1, 5)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 2, 1)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 3, 3)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 4, 2)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 5, 4)");

            // when & then - favoriteOrder 오름차순으로 정렬되어 반환되는지 확인
            RestAssured.given().port(port)
                    .queryParam("turipId", 1L)
                    .contentType(ContentType.JSON)
                    .when().get("/api/v1/turips/places")
                    .then()
                    .statusCode(200)
                    .body("turipPlaceCount", is(5))
                    .body("turipPlaces[0].place.name", is("장소2"))  // favoriteOrder 1
                    .body("turipPlaces[1].place.name", is("장소4"))  // favoriteOrder 2
                    .body("turipPlaces[2].place.name", is("장소3"))  // favoriteOrder 3
                    .body("turipPlaces[3].place.name", is("장소5"))  // favoriteOrder 4
                    .body("turipPlaces[4].place.name", is("장소1")); // favoriteOrder 5
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void readAllByFolder3() {
            // when & then
            RestAssured.given().port(port)
                    .queryParam("turipId", 1L)
                    .contentType(ContentType.JSON)
                    .when().get("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/api/v1/turips/places PUT 여러 폴더에 장소 찜 테스트")
    @Nested
    class UpdateFavoriteFolders {

        @DisplayName("장소 찜 폴더 업데이트에 성공한 경우 200 OK를 응답한다")
        @Test
        void updateFavoriteFolders() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId1 = testDataHelper.insertFavoriteFolder("폴더 1");
            Long folderId2 = testDataHelper.insertFavoriteFolder("폴더 2");
            Long folderId3 = testDataHelper.insertFavoriteFolder("폴더 3");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId2, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId3, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소1','https://naver.me/place1', '주소', 37.1234, 127.1234)");

            // 폴더 1에만 찜 되어 있는 상태
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 1, 1)");

            // when & then
            Map<String, Object> request = new HashMap<>();
            request.put("favoriteFolderIds", List.of(1L, 2L, 3L));

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().put("/api/v1/turips/places/{placeId}", 1L)
                    .then()
                    .statusCode(200);
        }
    }

    @DisplayName("/api/v1/turips/places/count GET 계정의 장소 찜 개수 조회 테스트")
    @Nested
    class ReadCountByAccount {

        @DisplayName("장소 찜 조회에 성공한 경우 200 OK를 응답한다.")
        @Test
        void readCountByAccount1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소1','https://naver.me/place1', '장소1 주소', 37.1234, 127.1234)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소2','https://naver.me/place2', '장소2 주소', 37.5678, 127.5678)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 2, 2)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .when().get("/api/v1/turips/places/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(2));
        }
    }

    @DisplayName("/api/v1/turips/places/turip-order PATCH 장소 찜 폴더 정렬 순서 변경 테스트")
    @Nested
    class UpdatePlaceOrder {

        @DisplayName("정렬 순서 변경에 성공한 경우 204 NO CONTENT를 응답한다")
        @Test
        void updatePlaceOrder1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소1','https://naver.me/place1', '장소1 주소', 37.1234, 127.1234)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소2','https://naver.me/place2', '장소2 주소', 37.5678, 127.5678)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (1, 2, 2)");

            // when & then
            Map<String, Object> request = new HashMap<>();
            request.put("turipPlaceIdsOrder", List.of(2L, 1L)); // 순서 바꾸기

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/places/turip-order")
                    .then()
                    .statusCode(204);
        }

        @DisplayName("favoriteFolderId에 대한 폴더를 찾을 수 없는 경우 404 NOT FOUND를 응답한다")
        @Test
        void updatePlaceOrder3() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);

            // when & then
            Map<String, Object> request = new HashMap<>();
            request.put("turipPlaceIdsOrder", List.of(1L));

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 999L)
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/places/turip-order")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("요청 회원 정보와 폴더 소유자의 정보가 일치하지 않는 경우 403 FORBIDDEN을 응답한다")
        @Test
        void updatePlaceOrder4() {
            // given
            Long ownerAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'ownerDeviceFid')",
                    ownerAccountId);
            Long requestAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'requestDeviceFid')",
                    requestAccountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, folderId, AccountRole.OWNER);

            // when & then
            Map<String, Object> request = new HashMap<>();
            request.put("turipPlaceIdsOrder", List.of(1L));

            RestAssured.given().port(port)
                    .header("device-fid", "requestDeviceFid")
                    .queryParam("turipId", 1L)
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/places/turip-order")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("존재하지 않는 favoritePlaceId가 포함된 경우 404 NOT FOUND를 응답한다")
        @Test
        void updatePlaceOrder5() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            // when & then
            Map<String, Object> request = new HashMap<>();
            request.put("turipPlaceIdsOrder", List.of(999L)); // 존재하지 않는 favoritePlaceId

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/places/turip-order")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("다른 폴더의 favoritePlaceId가 포함된 경우 400 Bad Request를 응답한다")
        @Test
        void updatePlaceOrder6() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId1 = testDataHelper.insertFavoriteFolder("폴더1");
            Long folderId2 = testDataHelper.insertFavoriteFolder("폴더2");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId2, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소1','https://naver.me/place1', '장소1 주소', 37.1234, 127.1234)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id, favorite_order) VALUES (2, 1, 1)"); // 폴더2에 속한 장소

            // when & then
            Map<String, Object> request = new HashMap<>();
            request.put("turipPlaceIdsOrder", List.of(1L)); // 폴더2의 favoritePlaceId를 폴더1에서 사용

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/places/turip-order")
                    .then()
                    .statusCode(400);
        }
    }

    @DisplayName("/api/v1/turips/places DELETE 장소 찜 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("장소 찜 삭제에 성공한 경우 204 NO CONTENT를 응답한다")
        @Test
        void delete1() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/api/v1/turips/places")
                    .then()
                    .statusCode(204);
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 403 FORBIDDEN을 응답한다")
        @Test
        void delete2() {
            // given
            Long accountId1 = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'may')", accountId1);
            // given
            Long accountId2 = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'cool')", accountId2);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId1, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "cool")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/api/v1/turips/places")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("deviceFid에 대한 회원이 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete3() {
            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "nonExistentDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete4() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 999L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete5() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 999L)
                    .contentType(ContentType.JSON)
                    .when().delete("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("삭제하려는 장소 찜이 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete6() {
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("turipId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/api/v1/turips/places")
                    .then()
                    .statusCode(404);
        }
    }
}
