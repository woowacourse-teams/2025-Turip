package turip.util.fixture;

import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;
import turip.article.domain.Tag;

public class TagFixture {

    public static Tag create() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return new Tag("태그" + uniqueId);
    }

    public static Tag createWithId(Long id) {
        Tag tag = create();
        ReflectionTestUtils.setField(tag, "id", id);
        return tag;
    }
}
