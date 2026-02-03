package turip.favorite.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.account.domain.Account;

@Getter
@Entity
@Table(name = "favorite_folder_account", uniqueConstraints = @UniqueConstraint(name = "uk_favorite_folder_account", columnNames = {
        "favorite_folder_id", "account_id"}))
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFolderAccount {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_folder_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_folder_account__favorite_folder"))
    private FavoriteFolder favoriteFolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_folder_account__account"))
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_role", nullable = false)
    private AccountRole accountRole;

    private FavoriteFolderAccount(FavoriteFolder favoriteFolder, Account account, AccountRole accountRole) {
        this.favoriteFolder = favoriteFolder;
        this.account = account;
        this.accountRole = accountRole;
    }

    public static FavoriteFolderAccount of(FavoriteFolder favoriteFolder, Account account, AccountRole accountRole) {
        return new FavoriteFolderAccount(favoriteFolder, account, accountRole);
    }

    public void updateAccount(Account account) {
        this.account = account;
    }
}
