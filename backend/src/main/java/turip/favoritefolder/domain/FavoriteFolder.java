package turip.favoritefolder.domain;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.member.domain.Member;

@Getter
@Entity
@Table(name = "favorite_folder", uniqueConstraints = {
        @UniqueConstraint(name = "uq_favorite_folder__member_name", columnNames = {"member_id", "name"})
})
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFolder {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorite_folder__member"))
    private Member member;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    private FavoriteFolder(Member member, String name, boolean isDefault) {
        this.member = member;
        this.name = name;
        this.isDefault = isDefault;
    }

    public static FavoriteFolder defaultFolderOf(Member member) {
        return new FavoriteFolder(member, "기본 폴더", true);
    }

    public static FavoriteFolder customFolderOf(Member member, String name) {
        String formattedName = formatName(name);
        validateName(formattedName);
        return new FavoriteFolder(member, name, false);
    }

    public static String formatName(String unformattedName) {
        return unformattedName.trim();
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("장소 찜 폴더 이름은 빈 칸이 될 수 없습니다.");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("장소 찜 폴더 이름은 최대 20글자 입니다.");
        }
    }

    public boolean isOwner(Member member) {
        return this.member.isSameDeviceId(member);
    }

    public void rename(String newName) {
        validateName(newName);
        this.name = newName;
    }
}
