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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.member.domain.Account;

@Getter
@Entity
@Table(name = "favorite_folder", uniqueConstraints = {
        @UniqueConstraint(name = "uq_favorite_folder__account_id_name", columnNames = {"account_id", "name"})
})
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFolder {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_folder__account"))
    private Account account;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    private FavoriteFolder(Account account, String name, boolean isDefault) {
        this.account = account;
        this.name = name;
        this.isDefault = isDefault;
    }

    public static FavoriteFolder defaultFolderOf(Account account) {
        return new FavoriteFolder(account, "기본 폴더", true);
    }

    public static FavoriteFolder customFolderOf(Account account, String name) {
        String formattedName = formatName(name);
        validateName(formattedName);
        return new FavoriteFolder(account, name, false);
    }

    public static String formatName(String unformattedName) {
        return unformattedName.trim();
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.FAVORITE_FOLDER_NAME_BLANK);
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException(ErrorTag.FAVORITE_FOLDER_NAME_TOO_LONG);
        }
    }

    public boolean isOwner(Account account) {
        System.out.println("account.id" + account.getId());
        System.out.println("this.account.id" + this.account.getId());
        return this.account.equals(account);
    }

    public void rename(String newName) {
        validateName(newName);
        this.name = newName;
    }
}
