package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
import turip.content.domain.Content;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.domain.ContentPendingStatus;
import turip.content.repository.ContentPendingRepository;
import turip.content.repository.ContentRepository;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.controller.dto.response.ContentPendingApprovalResponse;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.ContentFixture;

@ExtendWith(MockitoExtension.class)
class AdminContentPendingServiceTest {

    @InjectMocks
    private AdminContentPendingService adminContentPendingService;

    @Mock
    private ContentPendingRepository contentPendingRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private AdminContentService adminContentService;

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
            ContentPending foundContentPending = adminContentPendingService.getById(contentPending.getId());

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
            assertThatThrownBy(() -> adminContentPendingService.getById(contentPending.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.CONTENT_PENDING_NOT_FOUND.getMessage());
        }
    }

    @DisplayName("approve() 테스트")
    @Nested
    class Approve {

        @DisplayName("펜딩 콘텐츠를 승인하면 Content가 생성되고 ContentApprovalResponse를 반환한다")
        @Test
        void approve1() {
            // given
            given(contentPendingRepository.findById(contentPending.getId()))
                    .willReturn(Optional.of(contentPending));
            given(adminContentService.save(any(AdminContentSaveRequest.class)))
                    .willReturn(1L);

            Content mockContent = ContentFixture.createContentWithId(1L);
            given(contentRepository.findById(1L))
                    .willReturn(Optional.of(mockContent));

            // when
            ContentPendingApprovalResponse response = adminContentPendingService.approve(contentPending.getId(),
                    validatorAccount);

            // then
            assertAll(
                    () -> assertThat(response.contentId()).isEqualTo(1L),
                    () -> assertThat(response.contentPendingId()).isEqualTo(contentPending.getId()),
                    () -> verify(contentPendingRepository).findById(contentPending.getId()),
                    () -> verify(adminContentService).save(any(AdminContentSaveRequest.class)),
                    () -> verify(contentRepository).findById(1L)
            );
        }

        @DisplayName("존재하지 않는 펜딩 콘텐츠를 승인하려 하면 NotFoundException을 발생시킨다")
        @Test
        void approve2() {
            // given
            Long invalidId = 999L;
            given(contentPendingRepository.findById(invalidId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminContentPendingService.approve(invalidId, validatorAccount))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.CONTENT_PENDING_NOT_FOUND.getMessage());
        }

        @DisplayName("승인 후 생성된 Content를 찾을 수 없으면 NotFoundException을 발생시킨다")
        @Test
        void approve3() {
            // given
            given(contentPendingRepository.findById(contentPending.getId()))
                    .willReturn(Optional.of(contentPending));
            given(adminContentService.save(any(AdminContentSaveRequest.class)))
                    .willReturn(1L);
            given(contentRepository.findById(1L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminContentPendingService.approve(contentPending.getId(), validatorAccount))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.CONTENT_NOT_FOUND.getMessage());
        }
    }

    @DisplayName("reject() 테스트")
    @Nested
    class Reject {

        @DisplayName("펜딩 콘텐츠를 거절하면 상태를 REJECTED로 변경하고 거절 사유를 업데이트 한 뒤, ContentRejectResponse를 반환한다")
        @Test
        void reject1() {
            // given
            given(contentPendingRepository.findById(contentPending.getId()))
                    .willReturn(Optional.of(contentPending));
            ContentPendingRejectRequest request = new ContentPendingRejectRequest("거절 사유");

            // when
            adminContentPendingService.reject(contentPending.getId(), validatorAccount, request);

            // then
            assertAll(
                    () -> assertThat(contentPending.getStatus()).isEqualTo(ContentPendingStatus.REJECTED),
                    () -> assertThat(contentPending.getRejectReason()).isEqualTo(request.rejectReason())
            );
        }

        @DisplayName("존재하지 않는 펜딩 콘텐츠를 거절하려 하면 NotFoundException을 발생시킨다")
        @Test
        void reject2() {
            // given
            Long invalidId = 999L;
            given(contentPendingRepository.findById(invalidId))
                    .willReturn(Optional.empty());
            ContentPendingRejectRequest request = new ContentPendingRejectRequest("거절 사유");

            // when & then
            assertThatThrownBy(() -> adminContentPendingService.reject(invalidId, validatorAccount, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.CONTENT_PENDING_NOT_FOUND.getMessage());
        }
    }
}
