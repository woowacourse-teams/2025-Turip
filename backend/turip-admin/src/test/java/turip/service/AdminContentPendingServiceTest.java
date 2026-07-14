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
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ConflictException;
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
import turip.controller.dto.response.ContentPendingResponse;
import turip.controller.dto.response.MyCollectContentResponse;
import turip.controller.dto.response.PendingListResponse;
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

    @Mock
    private AccountRepository accountRepository;

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
            ContentPendingResponse response = adminContentPendingService.findById(contentPending.getId());

            // then
            assertAll(
                    () -> assertThat(response.collectorAccount().id()).isEqualTo(collectorAccount.getId()),
                    () -> assertThat(response.contentData().cityName()).isEqualTo(contentData.cityName()),
                    () -> assertThat(response.contentData().video().title()).isEqualTo(
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

    @DisplayName("getMyHistory() 테스트")
    @Nested
    class GetMyHistory {

        @DisplayName("관리자 자신이 수집한 콘텐츠 내역을 조회할 수 있다")
        @Test
        void getMyHistory1() {
            // given
            ContentPending pending1 = new ContentPending(contentData, collectorAccount, validatorAccount, null, null);
            ReflectionTestUtils.setField(pending1, "id", 1L);

            ContentPendingData.VideoData videoData2 = new ContentPendingData.VideoData(
                    "videoId456",
                    "Test Title 2",
                    "http://test2.url",
                    "Test Channel 2",
                    "http://channel2.image",
                    LocalDate.now()
            );
            ContentPendingData contentData2 = new ContentPendingData(
                    "Busan",
                    videoData2,
                    contentData.contentPlaces()
            );
            ContentPending pending2 = new ContentPending(contentData2, collectorAccount, validatorAccount, null, null);
            ReflectionTestUtils.setField(pending2, "id", 2L);
            ReflectionTestUtils.setField(pending2, "status", ContentPendingStatus.APPROVED);

            given(contentPendingRepository.findAllByCollectorAccountOrderByIdDesc(collectorAccount))
                    .willReturn(List.of(pending2, pending1));

            // when
            List<MyCollectContentResponse> responses = adminContentPendingService.getMyHistory(collectorAccount);

            // then
            assertAll(
                    () -> assertThat(responses).hasSize(2),
                    () -> assertThat(responses.get(0).id()).isEqualTo(2L),
                    () -> assertThat(responses.get(0).videoTitle()).isEqualTo("Test Title 2"),
                    () -> assertThat(responses.get(0).cityName()).isEqualTo("Busan"),
                    () -> assertThat(responses.get(0).status()).isEqualTo("APPROVED"),
                    () -> assertThat(responses.get(1).id()).isEqualTo(1L),
                    () -> assertThat(responses.get(1).videoTitle()).isEqualTo("Test Title"),
                    () -> assertThat(responses.get(1).cityName()).isEqualTo("Seoul"),
                    () -> assertThat(responses.get(1).status()).isEqualTo("PENDING"),
                    () -> verify(contentPendingRepository).findAllByCollectorAccountOrderByIdDesc(collectorAccount)
            );
        }

        @DisplayName("수집한 콘텐츠 내역이 없는 경우 빈 리스트를 반환한다")
        @Test
        void getMyHistory2() {
            // given
            given(contentPendingRepository.findAllByCollectorAccountOrderByIdDesc(collectorAccount))
                    .willReturn(List.of());

            // when
            List<MyCollectContentResponse> responses = adminContentPendingService.getMyHistory(collectorAccount);

            // then
            assertAll(
                    () -> assertThat(responses).isEmpty(),
                    () -> verify(contentPendingRepository).findAllByCollectorAccountOrderByIdDesc(collectorAccount)
            );
        }

        @DisplayName("거절된 콘텐츠의 경우 거절 사유를 포함하여 반환한다")
        @Test
        void getMyHistory3() {
            // given
            ContentPending rejectedPending = new ContentPending(contentData, collectorAccount, validatorAccount, null,
                    null);
            ReflectionTestUtils.setField(rejectedPending, "id", 3L);
            ReflectionTestUtils.setField(rejectedPending, "status", ContentPendingStatus.REJECTED);
            ReflectionTestUtils.setField(rejectedPending, "rejectReason", "부적절한 콘텐츠");

            given(contentPendingRepository.findAllByCollectorAccountOrderByIdDesc(collectorAccount))
                    .willReturn(List.of(rejectedPending));

            // when
            List<MyCollectContentResponse> responses = adminContentPendingService.getMyHistory(collectorAccount);

            // then
            assertAll(
                    () -> assertThat(responses).hasSize(1),
                    () -> assertThat(responses.get(0).id()).isEqualTo(3L),
                    () -> assertThat(responses.get(0).status()).isEqualTo("REJECTED"),
                    () -> assertThat(responses.get(0).rejectReason()).isEqualTo("부적절한 콘텐츠")
            );
        }
    }

    @DisplayName("findAll() 테스트")
    @Nested
    class FindAll {

        @DisplayName("PENDING 상태의 모든 콘텐츠를 ID 내림차순으로 조회할 수 있다")
        @Test
        void findAll1() {
            // given
            ContentPending pending1 = new ContentPending(contentData, collectorAccount, validatorAccount, null, null);
            ReflectionTestUtils.setField(pending1, "id", 1L);

            ContentPendingData.VideoData videoData2 = new ContentPendingData.VideoData(
                    "videoId456",
                    "Test Title 2",
                    "http://test2.url",
                    "Test Channel 2",
                    "http://channel2.image",
                    LocalDate.now()
            );
            ContentPendingData contentData2 = new ContentPendingData(
                    "Busan",
                    videoData2,
                    contentData.contentPlaces()
            );
            ContentPending pending2 = new ContentPending(contentData2, collectorAccount, validatorAccount, null, null);
            ReflectionTestUtils.setField(pending2, "id", 2L);

            given(contentPendingRepository.findAllByStatusOrderByIdDesc(ContentPendingStatus.PENDING))
                    .willReturn(List.of(pending2, pending1));

            // when
            List<PendingListResponse> responses = adminContentPendingService.findAll();

            // then
            assertAll(
                    () -> assertThat(responses).hasSize(2),
                    () -> assertThat(responses.get(0).id()).isEqualTo(2L),
                    () -> assertThat(responses.get(0).videoTitle()).isEqualTo("Test Title 2"),
                    () -> assertThat(responses.get(1).id()).isEqualTo(1L),
                    () -> assertThat(responses.get(1).videoTitle()).isEqualTo("Test Title"),
                    () -> verify(contentPendingRepository).findAllByStatusOrderByIdDesc(ContentPendingStatus.PENDING)
            );
        }
    }

    @DisplayName("findByValidatorAccount() 테스트")
    @Nested
    class FindByValidatorAccount {

        @DisplayName("PENDING 상태의 모든 콘텐츠를 ID 내림차순으로 조회할 수 있다")
        @Test
        void findByValidatorAccount1() {
            // given
            ContentPending pending1 = new ContentPending(contentData, collectorAccount, validatorAccount, null, null);
            ReflectionTestUtils.setField(pending1, "id", 1L);

            ContentPendingData.VideoData videoData2 = new ContentPendingData.VideoData(
                    "videoId456",
                    "Test Title 2",
                    "http://test2.url",
                    "Test Channel 2",
                    "http://channel2.image",
                    LocalDate.now()
            );
            ContentPendingData contentData2 = new ContentPendingData(
                    "Busan",
                    videoData2,
                    contentData.contentPlaces()
            );
            ContentPending pending2 = new ContentPending(contentData2, collectorAccount, null, null, null);
            ReflectionTestUtils.setField(pending2, "id", 2L);

            given(contentPendingRepository.findAllByStatusAndValidatorAccountOrderByIdDesc(ContentPendingStatus.PENDING,
                    validatorAccount))
                    .willReturn(List.of(pending1));

            // when
            List<PendingListResponse> responses = adminContentPendingService.findByValidatorAccount(validatorAccount);

            // then
            assertAll(
                    () -> assertThat(responses).hasSize(1),
                    () -> assertThat(responses.get(0).id()).isEqualTo(1L),
                    () -> assertThat(responses.get(0).videoTitle()).isEqualTo("Test Title"),
                    () -> verify(contentPendingRepository).findAllByStatusAndValidatorAccountOrderByIdDesc(
                            ContentPendingStatus.PENDING, validatorAccount)
            );
        }
    }


    @DisplayName("pending() 테스트")
    @Nested
    class Pending {

        @DisplayName("새로운 콘텐츠 검증 요청을 생성하고 ID를 반환한다")
        @Test
        void pending1() {
            // given
            Account admin2 = AccountFixture.createCustomAccount(2L, Role.ADMIN);
            Account admin3 = AccountFixture.createCustomAccount(3L, Role.ADMIN);

            given(contentPendingRepository.existsByVideoUrlAndStatus(contentData.video().url(),
                    ContentPendingStatus.PENDING))
                    .willReturn(false);
            given(accountRepository.findAllByRole(Role.ADMIN))
                    .willReturn(List.of(collectorAccount, admin2, admin3));
            given(contentPendingRepository.findTopByOrderByIdDesc())
                    .willReturn(Optional.empty());
            given(contentPendingRepository.save(any(ContentPending.class)))
                    .willAnswer(invocation -> {
                        ContentPending saved = invocation.getArgument(0);
                        ReflectionTestUtils.setField(saved, "id", 1L);
                        return saved;
                    });

            // when
            Long savedId = adminContentPendingService.pending(collectorAccount, contentData);

            // then
            assertAll(
                    () -> assertThat(savedId).isEqualTo(1L),
                    () -> verify(contentPendingRepository).existsByVideoUrlAndStatus(contentData.video().url(),
                            ContentPendingStatus.PENDING),
                    () -> verify(accountRepository).findAllByRole(Role.ADMIN),
                    () -> verify(contentPendingRepository).save(any(ContentPending.class))
            );
        }

        @DisplayName("이미 PENDING 상태로 존재하는 비디오 URL인 경우 ConflictException을 발생시킨다")
        @Test
        void pending2() {
            // given
            given(contentPendingRepository.existsByVideoUrlAndStatus(contentData.video().url(),
                    ContentPendingStatus.PENDING))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminContentPendingService.pending(collectorAccount, contentData))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(ErrorTag.DUPLICATE_PENDING_CONTENT.getMessage());
        }

        @DisplayName("validator 후보가 없는 경우 NotFoundException을 발생시킨다")
        @Test
        void pending3() {
            // given
            given(contentPendingRepository.existsByVideoUrlAndStatus(contentData.video().url(),
                    ContentPendingStatus.PENDING))
                    .willReturn(false);
            given(accountRepository.findAllByRole(Role.ADMIN))
                    .willReturn(List.of(collectorAccount));

            // when & then
            assertThatThrownBy(() -> adminContentPendingService.pending(collectorAccount, contentData))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.NO_AVAILABLE_VALIDATOR.getMessage());
        }

        @DisplayName("이전 validator가 있으면 다음 순서의 validator를 할당한다")
        @Test
        void pending4() {
            // given
            Account admin2 = AccountFixture.createCustomAccount(2L, Role.ADMIN);
            Account admin3 = AccountFixture.createCustomAccount(3L, Role.ADMIN);

            ContentPending lastPending = new ContentPending(contentData, collectorAccount, admin2, null, null);

            given(contentPendingRepository.existsByVideoUrlAndStatus(contentData.video().url(),
                    ContentPendingStatus.PENDING))
                    .willReturn(false);
            given(accountRepository.findAllByRole(Role.ADMIN))
                    .willReturn(List.of(collectorAccount, admin2, admin3));
            given(contentPendingRepository.findTopByOrderByIdDesc())
                    .willReturn(Optional.of(lastPending));
            given(contentPendingRepository.save(any(ContentPending.class)))
                    .willAnswer(invocation -> {
                        ContentPending saved = invocation.getArgument(0);
                        ReflectionTestUtils.setField(saved, "id", 2L);
                        return saved;
                    });

            // when
            Long savedId = adminContentPendingService.pending(collectorAccount, contentData);

            // then
            assertAll(
                    () -> assertThat(savedId).isEqualTo(2L),
                    () -> verify(contentPendingRepository).save(any(ContentPending.class))
            );
        }

        @DisplayName("마지막 validator가 리스트의 끝이면 처음부터 다시 할당한다")
        @Test
        void pending5() {
            // given
            Account admin2 = AccountFixture.createCustomAccount(2L, Role.ADMIN);
            Account admin3 = AccountFixture.createCustomAccount(3L, Role.ADMIN);

            ContentPending lastPending = new ContentPending(contentData, collectorAccount, admin3, null, null);

            given(contentPendingRepository.existsByVideoUrlAndStatus(contentData.video().url(),
                    ContentPendingStatus.PENDING))
                    .willReturn(false);
            given(accountRepository.findAllByRole(Role.ADMIN))
                    .willReturn(List.of(collectorAccount, admin2, admin3));
            given(contentPendingRepository.findTopByOrderByIdDesc())
                    .willReturn(Optional.of(lastPending));
            given(contentPendingRepository.save(any(ContentPending.class)))
                    .willAnswer(invocation -> {
                        ContentPending saved = invocation.getArgument(0);
                        ReflectionTestUtils.setField(saved, "id", 2L);
                        return saved;
                    });

            // when
            Long savedId = adminContentPendingService.pending(collectorAccount, contentData);

            // then
            assertAll(
                    () -> assertThat(savedId).isEqualTo(2L),
                    () -> verify(contentPendingRepository).save(any(ContentPending.class))
            );
        }
    }
}
