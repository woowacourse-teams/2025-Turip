package turip.util.fixture;

import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;
import turip.content.domain.Content;

public class ContentFixture {

    public static Content createContentWithId(Long id) {
        Content content = new Content(null, null, "Test Title", "http://test.url", LocalDate.now());
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }

    public static Content createContent() {
        return new Content(null, null, "Test Title", "http://test.url", LocalDate.now());
    }
}