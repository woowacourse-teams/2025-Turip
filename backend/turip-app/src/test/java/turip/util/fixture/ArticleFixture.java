package turip.util.fixture;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.article.domain.Article;

public class ArticleFixture {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1L);

    public static Article create() {
        return create(null);
    }

    public static Article create(Account author) {
        return createWithId(ID_GENERATOR.getAndIncrement(), author);
    }

    public static Article createWithId(Long id, Account author) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Article article = new Article(
                "제목" + uniqueId,
                "부제목" + uniqueId,
                "본문" + uniqueId,
                "https://turip.com/static/thumbnail-" + uniqueId,
                author,
                1
        );
        ReflectionTestUtils.setField(article, "id", id);
        return article;
    }
}
