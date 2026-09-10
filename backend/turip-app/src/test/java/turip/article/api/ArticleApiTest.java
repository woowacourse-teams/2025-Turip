package turip.article.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        testDataHelper.cleanDatabase();
    }

    private void insertArticle(long id, String title, boolean isPublished, int displayOrder) {
        jdbcTemplate.update(
                "INSERT INTO article (id, title, subtitle, content, thumbnail_url, display_order, is_published, created_at, updated_at) "
                        + "VALUES (?, ?, '부제목', '본문', null, ?, ?, NOW(), NOW())",
                id, title, displayOrder, isPublished);
    }

    @DisplayName("/api/v1/articles GET 아티클 목록 조회 테스트")
    @Nested
    class ReadArticles {

        @DisplayName("공개된 아티클 목록만 조회 시 200 OK 코드와 목록을 응답한다")
        @Test
        void readArticles1() {
            // given
            insertArticle(1L, "공개글", true, 1);
            insertArticle(2L, "비공개글", false, 2);

            // when & then
            RestAssured.given().port(port)
                    .when().get("/api/v1/articles")
                    .then()
                    .statusCode(200)
                    .body("articles.size()", is(1))
                    .body("articles[0].title", is("공개글"))
                    .body("loadable", is(false));
        }

        @DisplayName("lastId 이후 displayOrder를 기준으로 다음 페이지를 조회한다")
        @Test
        void readArticles2() {
            // given
            insertArticle(1L, "글1", true, 1);
            insertArticle(2L, "글2", true, 2);
            insertArticle(3L, "글3", true, 3);

            // when & then
            RestAssured.given().port(port)
                    .queryParam("size", 10)
                    .queryParam("lastId", 1)
                    .when().get("/api/v1/articles")
                    .then()
                    .statusCode(200)
                    .body("articles.size()", is(2))
                    .body("articles[0].title", is("글2"))
                    .body("articles[1].title", is("글3"));
        }
    }

    @DisplayName("/api/v1/articles/{id} GET 아티클 상세 조회 테스트")
    @Nested
    class ReadArticle {

        @DisplayName("공개된 아티클 조회 성공 시 200 OK 코드와 상세 정보를 응답한다")
        @Test
        void readArticle1() {
            // given
            insertArticle(1L, "제목", true, 1);

            // when & then
            RestAssured.given().port(port)
                    .when().get("/api/v1/articles/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("title", is("제목"))
                    .body("author", is((Object) null));
        }

        @DisplayName("비공개 아티클 조회 시 404 NOT FOUND 코드를 응답한다")
        @Test
        void readArticle2() {
            // given
            insertArticle(1L, "비공개", false, 1);

            // when & then
            RestAssured.given().port(port)
                    .when().get("/api/v1/articles/{id}", 1)
                    .then()
                    .statusCode(404)
                    .body("tag", is("ARTICLE_NOT_FOUND"));
        }

        @DisplayName("존재하지 않는 id 조회 시 404 NOT FOUND 코드를 응답한다")
        @Test
        void readArticle3() {
            // when & then
            RestAssured.given().port(port)
                    .when().get("/api/v1/articles/{id}", 999)
                    .then()
                    .statusCode(404)
                    .body("tag", is("ARTICLE_NOT_FOUND"));
        }
    }
}
