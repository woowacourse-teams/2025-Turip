package turip.favoritefolder.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.member.domain.Member;

class FavoriteFolderTest {

    @DisplayName("폴더의 소유자인지 확인할 수 있다")
    @Test
    void isOwner1() {
        // given
        Member owner = new Member("메이");
        Member nonOwner = new Member("하루");
        FavoriteFolder favoriteFolder = new FavoriteFolder(1L, owner, "폴더", false);

        // when & then
        assertAll(
                () -> assertThat(favoriteFolder.isOwner(owner)).isTrue(),
                () -> assertThat(favoriteFolder.isOwner(nonOwner)).isFalse()
        );
    }
}
