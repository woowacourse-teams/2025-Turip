package turip.article.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import turip.account.domain.Account;
import turip.common.domain.BaseTimeEntity;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Getter
@Entity
@Table(name = "article")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "subtitle", nullable = false, length = 200)
    private String subtitle;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_article__account"))
    private Account author;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    public Article(
            String title,
            String subtitle,
            String content,
            String thumbnailUrl,
            Account author,
            int displayOrder
    ) {
        validateTitle(title);
        validateSubtitle(subtitle);
        validateContent(content);

        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.author = author;
        this.displayOrder = displayOrder;
        this.isPublished = false;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.ARTICLE_TITLE_BLANK);
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException(ErrorTag.ARTICLE_TITLE_TOO_LONG);
        }
    }

    private void validateSubtitle(String subtitle) {
        if (subtitle == null || subtitle.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.ARTICLE_SUBTITLE_BLANK);
        }
        if (subtitle.length() > 200) {
            throw new IllegalArgumentException(ErrorTag.ARTICLE_SUBTITLE_TOO_LONG);
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.ARTICLE_CONTENT_BLANK);
        }
        if (content.getBytes(StandardCharsets.UTF_8).length > 65535) {
            throw new IllegalArgumentException(ErrorTag.ARTICLE_CONTENT_TOO_LONG);
        }
    }
}
