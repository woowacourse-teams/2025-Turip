package turip.favorite.domain;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.content.domain.Content;
import turip.account.domain.Account;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "favorite_content", uniqueConstraints = {
        @UniqueConstraint(name = "uq_favorite_content__account_id_content_id", columnNames = {"account_id",
                "content_id"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_content__account"))
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_content__content"))
    private Content content;

    public FavoriteContent(LocalDate createdAt, Account account, Content content) {
        this.content = content;
        this.account = account;
        this.createdAt = createdAt;
    }

    public void updateAccount(Account account) {
        this.account = account;
    }
}
