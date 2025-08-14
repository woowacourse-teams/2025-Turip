package turip.favoritefolder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import turip.member.domain.Member;

@Getter
@Setter
@Entity
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFolder {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    private String name;

    private boolean isDefault;

    public static FavoriteFolder defaultFolderOf(Member member) {
        return new FavoriteFolder(member, "기본 폴더", true);
    }

    public static FavoriteFolder customFolderOf(Member member, String name) {
        return new FavoriteFolder(member, name, false);
    }

    public boolean isOwner(Member member) {
        return this.member.isSameDeviceId(member);
    }

    private FavoriteFolder(Member member, String name, boolean isDefault) {
        this.member = member;
        this.name = name;
        this.isDefault = isDefault;
    }
}
