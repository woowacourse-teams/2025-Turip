package turip.content.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import turip.account.domain.Account;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.IllegalStateException;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.ContentPendingFixture;

class ContentPendingTest {

    @DisplayName("펜딩 콘텐츠 status 변경 테스트")
    @Nested
    class ContentPendingStatusTest {

        @DisplayName("approve() 단위 테스트")
        @Nested
        class Approve {

            @DisplayName("PENDING -> APPROVED 상태 변경 테스트")
            @Test
            void approve1() {
                // given
                Account collector = AccountFixture.createAdmin();
                Account validator = AccountFixture.createAdmin();
                ContentPending pendingContent = ContentPendingFixture.createPending(collector);
                Content content = null;

                // when
                pendingContent.approve(validator, content);

                // then
                assertThat(pendingContent.getStatus()).isEqualTo(ContentPendingStatus.APPROVED);
                assertThat(pendingContent.getValidatorAccount()).isEqualTo(validator);
                assertThat(pendingContent.getRejectReason()).isBlank();
            }

            @DisplayName("APPROVED -> APPROVED 상태로 변경할 수 없다.")
            @Test
            void approve2() {
                // given
                Account collector = AccountFixture.createAdmin();
                Account validator = AccountFixture.createAdmin();
                ContentPending pendingContent = ContentPendingFixture.createApproved(collector, validator, null);
                Content content = null;

                // when & then
                assertThatThrownBy(() -> pendingContent.approve(validator, content))
                        .isInstanceOf(IllegalStateException.class);
            }
        }


        @DisplayName("reject() 단위 테스트")
        @Nested
        class Reject {

            @DisplayName("PENDING -> REJECTED 상태 변경 테스트")
            @Test
            void reject1() {
                // given
                Account collector = AccountFixture.createAdmin();
                Account validator = AccountFixture.createAdmin();
                ContentPending pendingContent = ContentPendingFixture.createPending(collector);
                String rejectReason = "장소 하나가 빠졌어요";

                // when
                pendingContent.reject(validator, rejectReason);

                // then
                assertThat(pendingContent.getStatus()).isEqualTo(ContentPendingStatus.REJECTED);
                assertThat(pendingContent.getValidatorAccount()).isEqualTo(validator);
                assertThat(pendingContent.getRejectReason()).isEqualTo(rejectReason);
            }

            @DisplayName("APPROVED -> REJECTED 상태로 변경할 수 없다")
            @Test
            void reject2() {
                // given
                Account collector = AccountFixture.createAdmin();
                Account validator = AccountFixture.createAdmin();
                ContentPending pendingContent = ContentPendingFixture.createApproved(collector, validator, null);
                String rejectReason = "장소 하나가 빠졌어요";

                // when
                assertThatThrownBy(() -> pendingContent.reject(validator, rejectReason))
                        .isInstanceOf(IllegalStateException.class);
            }

            @DisplayName("거절 사유는 빈 값이 될 수 없다.")
            @ParameterizedTest
            @ValueSource(strings = {"", " ", "    "})
            void reject3(String rejectReason) {
                // given
                Account collector = AccountFixture.createAdmin();
                Account validator = AccountFixture.createAdmin();
                ContentPending pendingContent = ContentPendingFixture.createPending(collector);

                // when
                assertThatThrownBy(() -> pendingContent.reject(validator, rejectReason))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("거절 사유는 null이 될 수 없다.")
            @Test
            void reject4() {
                // given
                Account collector = AccountFixture.createAdmin();
                Account validator = AccountFixture.createAdmin();
                ContentPending pendingContent = ContentPendingFixture.createPending(collector);
                String rejectReason = null;

                // when
                assertThatThrownBy(() -> pendingContent.reject(validator, rejectReason))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("ContentPending 생성 시 PENDING 상태로 초기화")
        @Test
        void createWithPendingStatus() {
            // given
            ContentPending pendingContent = new ContentPending(null, null, null, null, null);

            // when & then
            assertThat(pendingContent.getStatus()).isEqualTo(ContentPendingStatus.PENDING);
        }
    }
}
