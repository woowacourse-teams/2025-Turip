package turip.article.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

class TagTest {

    @DisplayName("validateName() 단위 테스트")
    @Nested
    class ValidateName {

        @DisplayName("name이 비어있으면 예외를 발생시킨다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void validateName1(String invalidName) {
            // when & then
            assertThatThrownBy(() -> new Tag(invalidName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.TAG_NAME_BLANK.getMessage());
        }

        @DisplayName("name이 최대 길이(20자)를 초과하면 예외를 발생시킨다.")
        @Test
        void validateName2() {
            // given
            String tooLongName = "a".repeat(21);

            // when & then
            assertThatThrownBy(() -> new Tag(tooLongName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.TAG_NAME_TOO_LONG.getMessage());
        }
    }
}
