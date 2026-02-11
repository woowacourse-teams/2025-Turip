package turip.favorite.stream.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
class FavoriteFolderStreamApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM favorite_folder_account");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM guest");
        jdbcTemplate.update("DELETE FROM social_member");
        jdbcTemplate.update("DELETE FROM refresh_token");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM account");

        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE account ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder_account ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("폴더 상태 스트리밍 테스트")
    @Nested
    class Stream {

        @DisplayName("SSE 스트림 연결 성공 시 connect 이벤트를 보낸다.")
        @Test
        void stream1() throws Exception {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "turip", "TestPass1!");
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(folderId, accountId, AccountRole.OWNER);

            String accessToken = loginAndGetAccessToken("turip", "TestPass1!");
            URL url = new URL("http://localhost:" + port + "/api/v1/turips/" + folderId + "/stream");

            // when
            BufferedReader reader = connectStreamingAndGetInputBuffer(url, accessToken);
            List<String> events = readUntilEvent(reader, "connect");
            reader.close();

            // then
            assertThat(events).anyMatch(event -> event.contains("connect"));
            assertThat(events).anyMatch(event -> event.contains("data:") && event.contains("turipId"));
        }

        @DisplayName("SSE 스트림 연결 후 하트비트 이벤트를 받는다")
        @Test
        void stream2() throws Exception {
            // given
            Long accountId = testDataHelper.insertAccount();
            Long memberId = testDataHelper.insertMember(accountId, "test@example.com", false);
            testDataHelper.insertTuripMember(memberId, "turip", "TestPass1!");
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(folderId, accountId, AccountRole.OWNER);

            String accessToken = loginAndGetAccessToken("turip", "TestPass1!");
            URL url = new URL("http://localhost:" + port + "/api/v1/turips/" + folderId + "/stream");

            // when
            BufferedReader reader = connectStreamingAndGetInputBuffer(url, accessToken);
            List<String> events = readUntilEvent(reader, "heartbeat");
            reader.close();

            // then
            assertThat(events).anyMatch(event -> event.contains("heartbeat"));
        }

        @DisplayName("폴더 멤버가 아닌 경우 403 응답을 받는다")
        @Test
        void stream3() {
            // given
            Long ownerAccountId = testDataHelper.insertAccount();
            Long ownerMemberId = testDataHelper.insertMember(ownerAccountId, "test@example.com", false);
            testDataHelper.insertTuripMember(ownerMemberId, "owner", "TestPass1!");
            Long folderId = testDataHelper.insertFavoriteFolder("테스트 폴더");
            testDataHelper.insertFavoriteFolderAccount(folderId, ownerAccountId, AccountRole.OWNER);

            Long notOwnerAccountId = testDataHelper.insertAccount();
            Long notOwnerMemberId = testDataHelper.insertMember(notOwnerAccountId, "test2@example.com", false);
            testDataHelper.insertTuripMember(notOwnerMemberId, "notowner", "TestPass1!");

            String accessToken = loginAndGetAccessToken("notowner", "TestPass1!");

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/api/v1/turips/" + folderId + "/stream")
                    .then()
                    .statusCode(403);

        }
    }

    private String loginAndGetAccessToken(String loginId, String loginPassword) {
        Map<String, String> loginRequest = new HashMap<>(Map.of("loginId", loginId, "loginPassword", loginPassword));
        return RestAssured.given()
                .port(port)
                .header("device-fid", "testDeviceFid")
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/api/v1/auth/login/turip")
                .then()
                .statusCode(200)
                .extract()
                .cookie("accessToken");
    }

    private BufferedReader connectStreamingAndGetInputBuffer(URL url, String accessToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "text/event-stream");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(3000);
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    private List<String> readUntilEvent(BufferedReader reader, String targetEvent) throws IOException {
        List<String> currentEvent = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                if (currentEvent.stream().anyMatch(l -> l.contains(targetEvent))) {
                    return currentEvent;
                }
                currentEvent.clear();
            } else {
                currentEvent.add(line);
            }
        }
        return currentEvent;
    }
}
