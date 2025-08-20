package turip.favorite.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.content.domain.Content;
import turip.member.domain.Member;

@Getter
@Entity
@Table(name = "favorite_content", uniqueConstraints = {
        @UniqueConstraint(name = "uq_favorite_content__member_content", columnNames = {"member_id", "content_id"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteContent {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_content__member"))
    private Member member;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_content__content"))
    private Content content;

    public FavoriteContent(LocalDate createdAt, Member member, Content content) {
        this.createdAt = createdAt;
        this.member = member;
        this.content = content;
    }
}
