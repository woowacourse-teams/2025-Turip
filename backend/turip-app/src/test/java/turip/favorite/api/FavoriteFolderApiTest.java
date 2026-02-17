package turip.favorite.api;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (?, 1)",
                    favoriteFolder2);

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

    @DisplayName("GET /api/v1/turips/{turipId}/members 공유 찜폴더 참여자 목록 조회 테스트")
    @Nested
    class ReadMembersById {

        @DisplayName("공유 찜폴더 참여자 목록 조회에 성공한 경우 200 OK 코드와 참여자 정보를 응답한다")
        @Test
        void readMembersById1() {
            // given
            Long accountId1 = testDataHelper.insertAccount();
            Long accountId2 = testDataHelper.insertAccount();

            Long memberId1 = testDataHelper.insertMember(accountId1, "test1@example.com", false);
            Long memberId2 = testDataHelper.insertMember(accountId2, "test2@example.com", false);

            testDataHelper.insertTuripMember(memberId1, "member1", "TestPass1!");
            testDataHelper.insertTuripMember(memberId2, "member2", "TestPass1!");

            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId1);

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("함께 튜립", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId1, favoriteFolderId, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId2, favoriteFolderId, AccountRole.MEMBER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/turips/" + favoriteFolderId + "/members")
                    .then()
                    .statusCode(200)
                    .body("members.size()", is(2))
                    .body("members[0].nickname", notNullValue())
                    .body("members[1].nickname", notNullValue());
        }

        @DisplayName("공유 찜폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void readMembersById2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/turips/999/members")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("요청한 account가 찜폴더 참여자가 아닌 경우 403 FORBIDDEN을 응답한다")
        @Test
        void readMembersById3() {
            // given
            Long ownerAccountId = testDataHelper.insertAccount();
            Long nonOwnerAccountId = testDataHelper.insertAccount();
            Long ownerMemberId = testDataHelper.insertMember(ownerAccountId, "owner@example.com", false);
            Long nonOwnerMemberId = testDataHelper.insertMember(nonOwnerAccountId, "nonowner@example.com", false);
            testDataHelper.insertTuripMember(ownerMemberId, "owner", "OwnerPass1!");
            testDataHelper.insertTuripMember(nonOwnerMemberId, "nonowner", "NonOwnerPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("비공개 튜립", false, false);
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "nonMemberDeviceFid")
                    .when().get("/api/v1/turips/" + favoriteFolderId + "/members")
                    .then()
                    .statusCode(403);
        }
    }

    @DisplayName("POST /api/v1/turips/{turipId}/join 공유 찜폴더 참여 테스트")
    @Nested
    class Join {

        @DisplayName("공유 찜폴더 참여에 성공한 경우 200 OK 코드와 참여 정보를 응답한다")
        @Test
        void join1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("함께 튜립", false, true);
            Long ownerAccountId = testDataHelper.insertAccount();
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolderId, AccountRole.OWNER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/turips/" + favoriteFolderId + "/join")
                    .then()
                    .statusCode(200)
                    .body("id", is(notNullValue()))
                    .body("turipId", is(favoriteFolderId.intValue()))
                    .body("isShared", is(true))
                    .body("accountId", is(accountId.intValue()));
        }

        @DisplayName("찜폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void join2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/turips/999/join")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("개인 찜폴더에 참여하려는 경우 400 BAD REQUEST를 응답한다")
        @Test
        void join3() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("개인 폴더", false, false);
            Long ownerAccountId = testDataHelper.insertAccount();
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolderId, AccountRole.OWNER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/turips/" + favoriteFolderId + "/join")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("기본 찜폴더에 참여하려는 경우 400 BAD REQUEST를 응답한다")
        @Test
        void join4() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("기본 폴더", true, true);
            Long ownerAccountId = testDataHelper.insertAccount();
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolderId, AccountRole.OWNER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/turips/" + favoriteFolderId + "/join")
                    .then()
                    .statusCode(400);
        }
    }

    @DisplayName("/api/v1/turips/{turipId}/exit DELETE 공유 찜폴더 나가기 테스트")
    @Nested
    class ExitFolder {

        @DisplayName("공유 찜폴더에서 나가기에 성공한 경우 200 OK와 응답 정보를 반환한다 (마지막 참여자가 아닌 경우)")
        @Test
        void exit1() {
            // given
            Long accountId1 = testDataHelper.insertAccount();
            Long memberId1 = testDataHelper.insertMember(accountId1, "test1@example.com", false);
            testDataHelper.insertTuripMember(memberId1, "testuser1", "TestPass1!");

            Long accountId2 = testDataHelper.insertAccount();
            Long memberId2 = testDataHelper.insertMember(accountId2, "test2@example.com", false);
            testDataHelper.insertTuripMember(memberId2, "testuser2", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("함께 튜립", false, true);
            testDataHelper.insertFavoriteFolderAccount(accountId1, favoriteFolderId, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId2, favoriteFolderId, AccountRole.MEMBER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser2", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().delete("/api/v1/turips/" + favoriteFolderId + "/exit")
                    .then()
                    .statusCode(200)
                    .body("isDeleted", is(false));
        }

        @DisplayName("공유 찜폴더에서 나가기에 성공한 경우 200 OK와 응답 정보를 반환한다 (마지막 참여자인 경우)")
        @Test
        void exit2() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("함께 튜립", false, true);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().delete("/api/v1/turips/" + favoriteFolderId + "/exit")
                    .then()
                    .statusCode(200)
                    .body("isDeleted", is(true));
        }

        @DisplayName("찜폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void exit3() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().delete("/api/v1/turips/999/exit")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("개인 찜폴더에서 나가려는 경우 400 BAD REQUEST를 응답한다")
        @Test
        void exit4() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("개인 폴더", false, false);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().delete("/api/v1/turips/" + favoriteFolderId + "/exit")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("기본 찜폴더에서 나가려는 경우 400 BAD REQUEST를 응답한다")
        @Test
        void exit5() {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "testuser", "TestPass1!");

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("기본 폴더", true, true);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // 로그인하여 accessToken 얻기
            Map<String, String> loginRequest = new HashMap<>(
                    Map.of("loginId", "testuser", "loginPassword", "TestPass1!"));
            String accessToken = RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/turip")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookie("accessToken");

            // when & then
            RestAssured.given()
                    .port(port)
                    .header("device-fid", "testDeviceFid")
                    .header("Authorization", "Bearer " + accessToken)
                    .when().delete("/api/v1/turips/" + favoriteFolderId + "/exit")
                    .then()
                    .statusCode(400);
        }
    }

    @DisplayName("GET /api/v1/turips/{turipId} 튜립 상세 조회 테스트")
    @Nested
    class ReadById {

        @DisplayName("튜립 멤버가 상세 조회에 성공한 경우 200 OK와 튜립 상세 정보를 응답한다")
        @Test
        void readById1() {
            // given
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'testDeviceFid')", accountId);
            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("튜립 부산", false, true);
            testDataHelper.insertFavoriteFolderAccount(accountId, favoriteFolderId, AccountRole.OWNER);

            // 다른 멤버 추가
            Long accountId2 = testDataHelper.insertAccount();
            testDataHelper.insertFavoriteFolderAccount(accountId2, favoriteFolderId, AccountRole.MEMBER);

            Long accountId3 = testDataHelper.insertAccount();
            testDataHelper.insertFavoriteFolderAccount(accountId3, favoriteFolderId, AccountRole.MEMBER);

            Long accountId4 = testDataHelper.insertAccount();
            testDataHelper.insertFavoriteFolderAccount(accountId4, favoriteFolderId, AccountRole.MEMBER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/turips/" + favoriteFolderId)
                    .then()
                    .statusCode(200)
                    .body("id", is(favoriteFolderId.intValue()))
                    .body("name", is("튜립 부산"))
                    .body("isDefault", is(false))
                    .body("isShared", is(true))
                    .body("memberCount", is(4));
        }

        @DisplayName("해당 폴더에 참여한 멤버가 아닌 경우 403 FORBIDDEN을 응답한다")
        @Test
        void readById3() {
            // given
            Long ownerAccountId = testDataHelper.insertAccount();
            Long nonMemberAccountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, 'nonMemberDeviceFid')",
                    nonMemberAccountId);

            Long favoriteFolderId = testDataHelper.insertFavoriteFolder("비공개 튜립", false, false);
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, favoriteFolderId, AccountRole.OWNER);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "nonMemberDeviceFid")
                    .when().get("/api/v1/turips/" + favoriteFolderId)
                    .then()
                    .statusCode(403);
        }
    }

    @DisplayName("/api/v1/turips/invitation-tokens GET 튜립 초대 토큰 검증 테스트")
    @Nested
    class VerifyInvitation {

        @DisplayName("유효한 초대 토큰을 검증하고 200 OK와 튜립 정보를 응답한다")
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
                    .body("turipId", is(folderId.intValue()))
                    .body("alreadyJoined", is(true));
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
