package turip.content.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import turip.account.domain.Account;
import turip.util.fixture.AccountFixture;

class ContentPendingTest {

    @DisplayName("펜딩 콘텐츠 status 변경 테스트")
    @Nested
    class ContentPendingStatusTest {

        @DisplayName("PENDING -> APPROVED 상태 변경 테스트")
        @Test
        void approve() {
            // given
            Account collector = AccountFixture.createAdmin();
            Account validator = AccountFixture.createAdmin();
            ContentPending pendingContent = new ContentPending(
                    "{\"cityName\":\"서울\"}",
                    ContentPendingStatus.PENDING,
                    collector,
                    null,
                    null,
                    null
            );
            Content content = null;

            // when
            pendingContent.approve(validator, content);

            // then
            assertThat(pendingContent.getStatus()).isEqualTo(ContentPendingStatus.APPROVED);
            assertThat(pendingContent.getValidatorAccount()).isEqualTo(validator);
        }

        @DisplayName("PENDING -> REJECTED 상태 변경 테스트")
        @Test
        void reject() {
            // given
            Account collector = AccountFixture.createAdmin();
            Account validator = AccountFixture.createAdmin();
            ContentPending pendingContent = new ContentPending(
                    "{\"cityName\":\"서울\"}",
                    ContentPendingStatus.PENDING,
                    collector,
                    null,
                    null,
                    null
            );
            String rejectReason = "장소 하나가 빠졌어요";

            // when
            pendingContent.reject(validator, rejectReason);

            // then
            assertThat(pendingContent.getStatus()).isEqualTo(ContentPendingStatus.REJECTED);
            assertThat(pendingContent.getValidatorAccount()).isEqualTo(validator);
            assertThat(pendingContent.getRejectReason()).isEqualTo(rejectReason);
        }

        @DisplayName("ContentPending 생성 시 PENDING 상태로 초기화")
        @Test
        void createWithPendingStatus() {
            // given
            Account collector = AccountFixture.createUser();
            String contentData = "{\"cityName\":\"부산\",\"video\":{\"videoId\":\"abc123\"}}";

            // when
            ContentPending pendingContent = new ContentPending(
                    contentData,
                    ContentPendingStatus.PENDING,
                    collector,
                    null,
                    null,
                    null
            );

            // then
            assertThat(pendingContent.getStatus()).isEqualTo(ContentPendingStatus.PENDING);
        }
    }
}
