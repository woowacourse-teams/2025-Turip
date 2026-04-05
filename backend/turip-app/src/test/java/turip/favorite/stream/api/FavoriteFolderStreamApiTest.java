package turip.favorite.stream.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import turip.favorite.domain.AccountRole;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FavoriteFolderStreamApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        testDataHelper.cleanDatabase();
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
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            String accessToken = testDataHelper.createAccessToken(accountId);
            URL url = new URI("http://localhost:" + port + "/api/v1/turips/" + folderId + "/stream").toURL();

            // when
            List<String> events;
            try (BufferedReader reader = connectStreamingAndGetInputBuffer(url, accessToken);) {
                events = readUntilEvent(reader, "connect");
            }

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
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId, AccountRole.OWNER);

            String accessToken = testDataHelper.createAccessToken(accountId);
            URL url = new URI("http://localhost:" + port + "/api/v1/turips/" + folderId + "/stream").toURL();

            // when
            List<String> events;
            try (BufferedReader reader = connectStreamingAndGetInputBuffer(url, accessToken);) {
                events = readUntilEvent(reader, "heartbeat");
            }

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
            testDataHelper.insertFavoriteFolderAccount(ownerAccountId, folderId, AccountRole.OWNER);

            Long notOwnerAccountId = testDataHelper.insertAccount();
            Long notOwnerMemberId = testDataHelper.insertMember(notOwnerAccountId, "test2@example.com", false);
            testDataHelper.insertTuripMember(notOwnerMemberId, "notowner", "TestPass1!");

            String accessToken = testDataHelper.createAccessToken(notOwnerAccountId);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/api/v1/turips/" + folderId + "/stream")
                    .then()
                    .statusCode(403);

        }
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
