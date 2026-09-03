package turip.article.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.util.fixture.AccountFixture;

class ArticleTest {

    @DisplayName("validateTitle() 단위 테스트")
    @Nested
    class ValidateTitle {

        @DisplayName("title이 비어있으면 예외를 발생시킨다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void validateTitle1(String invalidTitle) {
            // given
            Account author = AccountFixture.createUser();

            // when & then
            assertThatThrownBy(() -> new Article(invalidTitle, "부제목", "본문", null, author, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.ARTICLE_TITLE_BLANK.getMessage());
        }

        @DisplayName("title이 최대 길이(100자)를 초과하면 예외를 발생시킨다.")
        @Test
        void validateTitle2() {
            // given
            Account author = AccountFixture.createUser();
            String tooLongTitle = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> new Article(tooLongTitle, "부제목", "본문", null, author, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.ARTICLE_TITLE_TOO_LONG.getMessage());
        }
    }

    @DisplayName("validateSubtitle() 단위 테스트")
    @Nested
    class ValidateSubtitle {

        @DisplayName("subtitle이 비어있으면 예외를 발생시킨다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void validateSubtitle1(String invalidSubtitle) {
            // given
            Account author = AccountFixture.createUser();

            // when & then
            assertThatThrownBy(() -> new Article("제목", invalidSubtitle, "본문", null, author, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.ARTICLE_SUBTITLE_BLANK.getMessage());
        }

        @DisplayName("subtitle이 최대 길이(200자)를 초과하면 예외를 발생시킨다.")
        @Test
        void validateSubtitle2() {
            // given
            Account author = AccountFixture.createUser();
            String tooLongSubtitle = "a".repeat(201);

            // when & then
            assertThatThrownBy(() -> new Article("제목", tooLongSubtitle, "본문", null, author, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.ARTICLE_SUBTITLE_TOO_LONG.getMessage());
        }
    }

    @DisplayName("validateContent() 단위 테스트")
    @Nested
    class ValidateContent {

        @DisplayName("content가 비어있으면 예외를 발생시킨다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void validateContent1(String invalidContent) {
            // given
            Account author = AccountFixture.createUser();

            // when & then
            assertThatThrownBy(() -> new Article("제목", "부제목", invalidContent, null, author, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.ARTICLE_CONTENT_BLANK.getMessage());
        }

        @DisplayName("content가 UTF-8 기준 최대 바이트(65535)를 초과하면 예외를 발생시킨다.")
        @Test
        void validateContent2() {
            // given
            Account author = AccountFixture.createUser();
            // 한글 1글자는 UTF-8 기준 3바이트이므로 21846자면 65538바이트로 한도 초과
            String tooLongContent = "가".repeat(21846);

            // when & then
            assertThatThrownBy(() -> new Article("제목", "부제목", tooLongContent, null, author, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorTag.ARTICLE_CONTENT_TOO_LONG.getMessage());
        }
    }
}
