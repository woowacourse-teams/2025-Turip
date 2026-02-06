package turip.favorite.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

class FavoriteFolderTest {

    @DisplayName("폴더 이름 앞, 뒤의 공백을 제거할 수 있다")
    @Test
    void formatName1() {
        // given
        String unformattedName = "   튜립  ";
        String formattedName = "튜립";

        // when & then
        assertThat(FavoriteFolder.formatName(unformattedName))
                .isEqualTo(formattedName);
    }

    @DisplayName("폴더 이름 유효성 검사 테스트")
    @Nested
    class ValidateName {

        @DisplayName("폴더 이름이 공백인 경우 IllegalArgumentException을 발생시킨다")
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "    "})
        void validateName1(String name) {
            // when & then
            assertThatThrownBy(() -> FavoriteFolder.customFolderOf(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NAME_BLANK.getMessage());
        }

        @DisplayName("폴더 이름이 20자를 초과하는 경우 IllegalArgumentException을 발생시킨다")
        @Test
        void validateName2() {
            // given
            String nameOfLength20 = "20글자폴더입니다용20글자폴더입니다용";
            String nameOfLength21 = "21글자폴더입니다용21글자폴더입니다용~";

            // when & then
            assertAll(
                    () -> assertDoesNotThrow(() -> FavoriteFolder.customFolderOf(nameOfLength20)),
                    () -> assertThatThrownBy(() -> FavoriteFolder.customFolderOf(nameOfLength21))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrorTag.FAVORITE_FOLDER_NAME_TOO_LONG.getMessage())
            );
        }
    }
}
