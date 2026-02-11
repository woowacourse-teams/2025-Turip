package turip.favorite.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
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
class FavoriteFolderApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM place");
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

    @DisplayName("/api/v1/turips POST 장소 찜 폴더 생성 테스트")
    @Nested
    class Create {

        @DisplayName("커스텀 찜 폴더 생성에 성공한 경우 201 CREATED 코드와 생성된 폴더 정보를 응답한다")
        @Test
        void create1() {
            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "대구 맛집 모음"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips")
                    .then()
                    .statusCode(201);
        }

        @DisplayName("중복된 찜 폴더 이름이 존재하여 생성에 실패한 경우 409 CONFLICT 코드를 응답한다")
        @Test
        void create2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            String folderName = "대구 맛집 모음";
            Long folderId = testDataHelper.insertFavoriteFolder(folderName);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", folderName));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips")
                    .then()
                    .statusCode(409);
        }

        @DisplayName("폴더 이름이 형식에 맞지 않는 경우 400 BAD REQUEST 코드를 응답한다")
        @Test
        void create3() {
            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "21글자폴더입니다용21글자폴더입니다용~"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/api/v1/turips")
                    .then()
                    .statusCode(400);
        }
    }

    @DisplayName("/api/v1/turips GET 특정 회원의 장소 찜 폴더 조회 테스트")
    @Nested
    class ReadAllByMember {

        @DisplayName("조회에 성공한 경우 200 OK 코드와 폴더 목록을 응답한다")
        @Test
        void readAllByMember1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder1 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long favoriteFolder2 = testDataHelper.insertFavoriteFolder("대구 맛집 모음", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder2, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/turips")
                    .then()
                    .statusCode(200)
                    .body("turips.size()", is(2))
                    .body("turips[0].id", is(1))
                    .body("turips[0].accountId", is(accountId.intValue()))
                    .body("turips[0].name", is("기본 폴더"))
                    .body("turips[0].isDefault", is(true))
                    .body("turips[1].id", is(2))
                    .body("turips[1].accountId", is(accountId.intValue()))
                    .body("turips[1].name", is("대구 맛집 모음"))
                    .body("turips[1].isDefault", is(false));
        }

        @DisplayName("저장되지 않은 회원이 조회를 시도하는 경우 200 OK 코드와 기본 폴더 정보를 응답한다")
        @Test
        void readAllByMember2() {
            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "newDeviceFid")
                    .when().get("/api/v1/turips")
                    .then()
                    .statusCode(200)
                    .body("turips.size()", is(1))
                    .body("turips[0].accountId", is(1))
                    .body("turips[0].name", is("기본 폴더"))
                    .body("turips[0].isDefault", is(true));
        }
    }

    @DisplayName("/api/v1/turips/turip-status GET 특정 회원의 장소 찜 폴더와 찜 여부 조회 테스트")
    @Nested
    class ReadAllWithFavoriteStatusByDeviceId {

        @DisplayName("조회에 성공한 경우 200 OK 코드와 폴더 목록, 찜 여부를 응답한다")
        @Test
        void readAllWithFavoriteStatusByDeviceId1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder1 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long favoriteFolder2 = testDataHelper.insertFavoriteFolder("대구 맛집 모음", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder2, AccountRole.OWNER);
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (2, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("placeId", 1L)
                    .when().get("/api/v1/turips/turip-status")
                    .then()
                    .statusCode(200)
                    .body("turips.size()", is(2))
                    .body("turips[0].isTuripPlace", is(false))
                    .body("turips[1].isTuripPlace", is(true));
        }

        @DisplayName("저장되지 않은 회원이 조회를 시도하는 경우 200 OK 코드와 기본 폴더 정보를 응답한다")
        @Test
        void readAllWithFavoriteStatusByDeviceId2() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "newDeviceFid")
                    .queryParam("placeId", 1L)
                    .when().get("/api/v1/turips/turip-status")
                    .then()
                    .statusCode(200)
                    .body("turips[0].name", is("기본 폴더"))
                    .body("turips[0].isDefault", is(true))
                    .body("turips[0].isTuripPlace", is(false));
        }

        @DisplayName("placeId에 대한 장소를 찾을 수 없는 경우 404 NOT FOUND를 응답한다")
        @Test
        void readAllWithFavoriteStatusByDeviceId3() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("placeId", 1L)
                    .when().get("/api/v1/turips/turip-status")
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/api/v1/turips PATCH 폴더 이름 수정 테스트")
    @Nested
    class UpdateName {

        @DisplayName("수정에 성공한 경우 200 OK 코드와 수정된 폴더 정보를 응답한다")
        @Test
        void updateName1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("변경전 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "변경된 폴더"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/1")
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("accountId", is(accountId.intValue()))
                    .body("name", is("변경된 폴더"))
                    .body("isDefault", is(false));
        }

        @DisplayName("favoriteFolderId에 대한 폴더를 찾을 수 없는 경우 404 NOT FOUND를 응답한다")
        @Test
        void updateName3() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("변경전 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "변경된 폴더"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/999")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("요청 회원 정보와 폴더 소유자의 정보가 일치하지 않는 경우 403 FORBIDDEN을 응답한다")
        @Test
        void updateName4() {
            // given
            Long ownerAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'ownerDeviceFid')",
                    ownerAccountId);
            Long requestAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'requestDeviceFid')",
                    requestAccountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("변경전 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolder, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "변경된 폴더"));
            RestAssured.given().port(port)
                    .header("device-fid", "requestDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/1")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("중복된 찜 폴더 이름이 존재하여 수정에 실패한 경우 409 CONFLICT 코드를 응답한다")
        @Test
        void updateName5() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("변경전 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder, AccountRole.OWNER);
            Long favoriteFolder2 = testDataHelper.insertFavoriteFolder("다른 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder2, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "다른 폴더"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/1")
                    .then()
                    .statusCode(409);
        }

        @DisplayName("변경하려는 폴더 이름이 형식에 맞지 않는 경우 400 BAD REQUEST 코드를 응답한다")
        @Test
        void updateName6() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("변경전 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "21글자폴더입니다용21글자폴더입니다용~"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/1")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("변경하려는 폴더가 기본 폴더인 경우 400 BAD REQUEST 코드를 응답한다")
        @Test
        void updateName7() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolder = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolder, AccountRole.OWNER);

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "변경된 폴더"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().patch("/api/v1/turips/1")
                    .then()
                    .statusCode(400);
        }
    }

    @DisplayName("/api/v1/turips DELETE 장소 찜 폴더 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("삭제에 성공한 경우 204 NO CONTENT 코드를 응답한다")
        @Test
        void delete1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("삭제할 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().delete("/api/v1/turips/" + favoriteFolderId)
                    .then()
                    .statusCode(204);
        }

        @DisplayName("favoriteFolderId에 대한 폴더를 찾을 수 없는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("삭제할 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().delete("/api/v1/turips/999")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("요청 회원 정보와 폴더 소유자의 정보가 일치하지 않는 경우 403 FORBIDDEN을 응답한다")
        @Test
        void delete4() {
            // given
            Long ownerAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'ownerDeviceFid')",
                    ownerAccountId);
            Long requestAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'requestDeviceFid')",
                    requestAccountId);
            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("삭제할 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "requestDeviceFid")
                    .when().delete("/api/v1/turips/1")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("삭제하려는 폴더가 기본 폴더인 경우 400 BAD REQUEST를 응답한다")
        @Test
        void delete5() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().delete("/api/v1/turips/1")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("삭제하려는 폴더가 공유 폴더인 경우 400 BAD REQUEST를 응답한다")
        @Test
        void delete6() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("공유 폴더", false, true);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().delete("/api/v1/turips/1")
                    .then()
                    .statusCode(400);
        }
    }

    @DisplayName("/api/v1/turips/invitation-tokens GET 튜립 초대 토큰 검증 테스트")
    @Nested
    class VerifyInvitation {

        @DisplayName("유효한 초대 토큰를 검증하고 200 OK와 튜립 정보를 응답한다")
        @Test
        void verifyInvitation1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            String accessToken = testDataHelper.createAccessToken(accountId);

            Long folderId = testDataHelper.insertFavoriteFolder("내 튜립");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            String invitationToken = testDataHelper.createInvitationToken(accountId, folderId);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + accessToken)
                    .queryParam("token", invitationToken)
                    .when().get("/api/v1/turips/invitation-tokens")
                    .then()
                    .statusCode(200)
                    .body("turipId", is(folderId.intValue()));
        }

        @DisplayName("유효하지 않은 초대 토큰를 검증하고 400 BAD REQUEST를 응답한다")
        @Test
        void verifyInvitation2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            String accessToken = testDataHelper.createAccessToken(accountId);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + accessToken)
                    .queryParam("token", "invalid-token")
                    .when().get("/api/v1/turips/invitation-tokens")
                    .then()
                    .statusCode(400);
        }
    }
}
