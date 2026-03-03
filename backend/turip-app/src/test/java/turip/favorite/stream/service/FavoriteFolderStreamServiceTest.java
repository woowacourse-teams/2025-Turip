package turip.favorite.stream.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.domain.event.ActionType;
import turip.favorite.service.FavoriteFolderService;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.MemberFixture;

@ExtendWith(MockitoExtension.class)
class FavoriteFolderStreamServiceTest {

    @InjectMocks
    private FavoriteFolderStreamService favoriteFolderStreamService;

    @Mock
    private FavoriteFolderService favoriteFolderService;

    @Mock
    private ScheduledExecutorService scheduler;

    @DisplayName("emitter 생성 테스트")
    @Nested
    class CreateEmitter {

        @DisplayName("연결하고자 하는 폴더가 존재하지 않는 경우 NotFoundException이 발생한다")
        @Test
        void createEmitter1() {
            // given
            Long folderId = 1L;
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "test@example.com", false);

            willThrow(new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND))
                    .given(favoriteFolderService)
                    .validateFolderMembership(folderId, member);

            // when & then
            assertThatThrownBy(() -> favoriteFolderStreamService.createEmitter(folderId, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("연결하고자 하는 폴더가 참여중인 폴더가 아닌 경우 ForbiddenException이 발생한다")
        @Test
        void createEmitter2() {
            // given
            Long folderId = 1L;
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "test@example.com", false);

            willThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .given(favoriteFolderService)
                    .validateFolderMembership(folderId, member);

            // when & then
            assertThatThrownBy(() -> favoriteFolderStreamService.createEmitter(folderId, member))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(ErrorTag.FORBIDDEN.getMessage());
        }
    }

    @DisplayName("폴더 업데이트 이벤트 전송 테스트")
    @Nested
    class SendFolderUpdateEvents {

        @DisplayName("폴더에 연결된 emitter들에게 폴더 업데이트 이벤트를 전송할 수 있다")
        @Test
        void sendFolderUpdateEvents1() throws IOException {
            // given
            Long folderId = 1L;
            ActionType actionType = ActionType.PLACE_ADDED;

            SseEmitter emitter1 = mock(SseEmitter.class);
            SseEmitter emitter2 = mock(SseEmitter.class);

            Map<Long, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();
            Map<String, SseEmitter> folderEmitters = new ConcurrentHashMap<>();
            folderEmitters.put("1:1", emitter1);
            folderEmitters.put("1:2", emitter2);
            emitters.put(folderId, folderEmitters);

            ReflectionTestUtils.setField(favoriteFolderStreamService, "emitters", emitters);

            // when
            favoriteFolderStreamService.sendFolderUpdateEvents(folderId, actionType);

            // then
            verify(emitter1, times(1)).send(any(SseEmitter.SseEventBuilder.class));
            verify(emitter2, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        }
    }

    @DisplayName("하트비트 테스트")
    @Nested
    class Heartbeat {

        @DisplayName("하트비트 스케줄을 등록할 수 있다.")
        @Test
        void startHeartbeat() {
            // given
            String emitterKey = "1:1";
            SseEmitter emitter = mock(SseEmitter.class);
            @SuppressWarnings("unchecked")
            ScheduledFuture<Object> scheduledFuture = (ScheduledFuture<Object>) mock(ScheduledFuture.class);
            Long heartbeatInterval = 30L;

            ReflectionTestUtils.setField(favoriteFolderStreamService, "heartbeatInterval", heartbeatInterval);
            given(scheduler.scheduleAtFixedRate(
                    any(Runnable.class),
                    eq(heartbeatInterval),
                    eq(heartbeatInterval),
                    eq(TimeUnit.SECONDS)
            )).willReturn((ScheduledFuture) scheduledFuture);

            // when
            ReflectionTestUtils.invokeMethod(favoriteFolderStreamService, "startHeartbeat", emitterKey, emitter);

            // then
            verify(scheduler, times(1)).scheduleAtFixedRate(
                    any(Runnable.class),
                    eq(heartbeatInterval),
                    eq(heartbeatInterval),
                    eq(TimeUnit.SECONDS)
            );

            Map<String, ScheduledFuture<?>> heartbeatSchedules = (Map<String, ScheduledFuture<?>>) ReflectionTestUtils.getField(
                    favoriteFolderStreamService, "heartbeatSchedules");
            assertThat(heartbeatSchedules).containsKey(emitterKey);
        }

        @DisplayName("하트비트 스케줄을 취소할 수 있다")
        @Test
        void cancelHeartbeat() {
            // given
            String emitterKey = "1:1";
            @SuppressWarnings("unchecked")
            ScheduledFuture<Object> scheduledFuture = (ScheduledFuture<Object>) mock(ScheduledFuture.class);

            Map<String, ScheduledFuture<?>> heartbeatSchedules = new ConcurrentHashMap<>();
            heartbeatSchedules.put(emitterKey, scheduledFuture);
            ReflectionTestUtils.setField(favoriteFolderStreamService, "heartbeatSchedules", heartbeatSchedules);

            // when
            ReflectionTestUtils.invokeMethod(favoriteFolderStreamService, "cancelHeartbeat", emitterKey);

            // then
            verify(scheduledFuture, times(1)).cancel(false);
            assertThat(heartbeatSchedules).doesNotContainKey(emitterKey);
        }
    }
}
