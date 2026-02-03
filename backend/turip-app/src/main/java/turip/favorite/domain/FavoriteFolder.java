package turip.favorite.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Getter
@Entity
@Table(name = "favorite_folder")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFolder {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "member_count", nullable = false)
    private int memberCount;

    @Column(name = "is_shared", nullable = false)
    private boolean isShared;

    private FavoriteFolder(String name, boolean isDefault, int memberCount) {
        this.name = name;
        this.isDefault = isDefault;
        this.memberCount = memberCount;
        this.isShared = false;
    }

    public static FavoriteFolder defaultFolderOf() {
        return new FavoriteFolder("기본 폴더", true, 1);
    }

    public static FavoriteFolder customFolderOf(String name) {
        String formattedName = formatName(name);
        validateName(formattedName);
        return new FavoriteFolder(formattedName, false, 1);
    }

    public static String formatName(String unformattedName) {
        return unformattedName.trim();
    }

    public void rename(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void shareFolder() {
        this.isShared = true;
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.FAVORITE_FOLDER_NAME_BLANK);
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException(ErrorTag.FAVORITE_FOLDER_NAME_TOO_LONG);
        }
    }
}
