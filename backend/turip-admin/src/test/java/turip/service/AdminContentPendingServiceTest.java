package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.repository.ContentPendingRepository;
import turip.util.fixture.AccountFixture;

@ExtendWith(MockitoExtension.class)
class AdminContentPendingServiceTest {

    @InjectMocks
    private AdminContentPendingService adminContentPendingService;

    @Mock
    private ContentPendingRepository contentPendingRepository;

    private ContentPending contentPending;
    private Account validatorAccount;
    private Account collectorAccount;
    private ContentPendingData contentData;

    @BeforeEach
    void setUp() {
        // 계정 생성
        collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
        validatorAccount = AccountFixture.createCustomAccount(2L, Role.ADMIN);

        // ContentPendingData 생성
        ContentPendingData.VideoData videoData = new ContentPendingData.VideoData(
                "videoId123",
                "Test Title",
                "http://test.url",
                "Test Channel",
                "http://channel.image",
                LocalDate.now()
        );
        ContentPendingData.PlaceData placeData = new ContentPendingData.PlaceData(
                "Test Place",
                "http://place.url",
                "Test Address",
                37.5665,
                126.9780,
                "Restaurant"
        );
        ContentPendingData.ContentPlaceData contentPlaceData = new ContentPendingData.ContentPlaceData(
                1,
                1,
                "00:30",
                placeData
        );
        contentData = new ContentPendingData(
                "Seoul",
                videoData,
                List.of(contentPlaceData)
        );

        // ContentPending 생성
        contentPending = new ContentPending(contentData, collectorAccount, validatorAccount, null, null);
        ReflectionTestUtils.setField(contentPending, "id", 1L);
    }

    @DisplayName("findById() 테스트")
    @Nested
    class FindById {

        @DisplayName("id를 기반으로 ContentPending의 상세 정보를 불러올 수 있다")
        @Test
        void findById1() {
            // given
            given(contentPendingRepository.findById(contentPending.getId()))
                    .willReturn(Optional.of(contentPending));

            // when
            ContentPending foundContentPending = adminContentPendingService.findById(contentPending.getId());

            // then
            assertAll(
                    () -> assertThat(foundContentPending.getCollectorAccount()).isEqualTo(collectorAccount),
                    () -> assertThat(foundContentPending.getContentData().cityName()).isEqualTo(contentData.cityName()),
                    () -> assertThat(foundContentPending.getContentData().video().title()).isEqualTo(
                            contentData.video().title())
            );
        }

        @DisplayName("id에 대한 ContentPending을 찾을 수 없는 경우 NotFoundException을 발생시킨다")
        @Test
        void findById2() {
            // given
            given(contentPendingRepository.findById(contentPending.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminContentPendingService.findById(contentPending.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.CONTENT_PENDING_NOT_FOUND.getMessage());
        }
    }
}
