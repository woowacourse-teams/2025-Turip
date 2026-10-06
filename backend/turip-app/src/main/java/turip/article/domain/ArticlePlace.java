package turip.article.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import turip.place.domain.Place;

@Getter
@Entity
@EqualsAndHashCode(of = {"article", "place"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article_place", uniqueConstraints = {
        @UniqueConstraint(name = "uq_article_place__article_id_place_id", columnNames = {"article_id", "place_id"})
})
public class ArticlePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "article_id", nullable = false, foreignKey = @ForeignKey(name = "fk_article_place__article"))
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false, foreignKey = @ForeignKey(name = "fk_article_place__place"))
    private Place place;

    public ArticlePlace(Article article, Place place) {
        this.article = article;
        this.place = place;
    }
}
