package turip.favorite.stream.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import turip.account.domain.Member;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.InternalServerException;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.service.FavoriteFolderAccountService;
import turip.favorite.service.FavoriteFolderService;
import turip.favorite.stream.controller.dto.response.ConnectStreamResponse;
import turip.favorite.stream.controller.dto.response.FolderUpdateStreamResponse;
import turip.favorite.stream.controller.dto.response.HeartbeatStreamResponse;

@Slf4j
@Service
public class FavoriteFolderStreamService {

    private static final Long DEFAULT_TIMEOUT = 3 * 60 * 1000L; // 3분
    private static final String SSE_LOG_PREFIX = "[SSE] ";
    private final Map<Long, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> heartbeatSchedules = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    @Value("${sse.heartbeat.interval:30}")
    private Long heartbeatInterval;

    private final FavoriteFolderService favoriteFolderService;
    private final FavoriteFolderAccountService favoriteFolderAccountService;

    public FavoriteFolderStreamService(
            FavoriteFolderService favoriteFolderService,
            FavoriteFolderAccountService favoriteFolderAccountService,
            ScheduledExecutorService scheduler
    ) {
        this.favoriteFolderService = favoriteFolderService;
        this.favoriteFolderAccountService = favoriteFolderAccountService;
        this.scheduler = scheduler;
    }

    public SseEmitter createEmitter(Long favoriteFolderId, Member member) {
        validateIfMemberJoiningFavoriteFolder(favoriteFolderId, member);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterKey = getEmitterKey(favoriteFolderId, member.getId());

        registerEmitterCallbacks(favoriteFolderId, emitter, emitterKey);
        addEmitter(favoriteFolderId, emitterKey, emitter);

        sendConnectEvent(favoriteFolderId, emitter, emitterKey);
        return emitter;
    }

    public void sendFolderUpdateEvents(Long favoriteFolderId, ActionType actionType) {
        Map<String, SseEmitter> folderEmitters = emitters.get(favoriteFolderId);
        if (folderEmitters == null || folderEmitters.isEmpty()) {
            log.info(SSE_LOG_PREFIX + "폴더에 연결된 사용자 없음, folderId: {}", favoriteFolderId);
            return;
        }
        log.info(SSE_LOG_PREFIX + "폴더 업데이트 이벤트 전송 시작, folderId: {}, 연결된 사용자 수: {}", favoriteFolderId,
                folderEmitters.size());
        folderEmitters.values()
                .forEach(emitter -> sendFolderUpdateEvent(favoriteFolderId, actionType, emitter));
    }

    private void validateIfMemberJoiningFavoriteFolder(Long favoriteFolderId, Member member) {
        FavoriteFolder favoriteFolder = favoriteFolderService.getById(favoriteFolderId);
        favoriteFolderAccountService.validateMembership(member.getAccount(), favoriteFolder);
    }

    private String getEmitterKey(Long favoriteFolderId, Long memberId) {
        return favoriteFolderId + ":" + memberId + ":" + System.currentTimeMillis();
    }

    private void registerEmitterCallbacks(Long favoriteFolderId, SseEmitter emitter, String emitterKey) {
        emitter.onCompletion(() -> {
            cancelHeartbeat(emitterKey);
            removeEmitter(favoriteFolderId, emitterKey);
            log.info(SSE_LOG_PREFIX + "클라이언트 연결 해제, emitterKey: {}", emitterKey);
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            log.info(SSE_LOG_PREFIX + "연결 타임아웃, emitterKey: {}", emitterKey);
        });

        emitter.onError(e -> {
            cancelHeartbeat(emitterKey);
            removeEmitter(favoriteFolderId, emitterKey);
            log.error(SSE_LOG_PREFIX + "연결 에러, emitterKey: {}", emitterKey, e);
        });
    }

    private void addEmitter(Long favoriteFolderId, String emitterKey, SseEmitter emitter) {
        emitters.compute(favoriteFolderId, (key, folderEmitters) -> {
            if (folderEmitters == null) {
                folderEmitters = new ConcurrentHashMap<>();
            }
            folderEmitters.put(emitterKey, emitter);
            return folderEmitters;
        });
    }

    private void sendConnectEvent(Long favoriteFolderId, SseEmitter emitter, String emitterKey) {
        try {
            ConnectStreamResponse response = ConnectStreamResponse.from(favoriteFolderId);

            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(StreamEventType.CONNECT.getName())
                    .data(response);

            emitter.send(event);
            startHeartbeat(emitterKey, emitter);
            log.info(SSE_LOG_PREFIX + "SSE 연결 성공, emitterKey: {}", emitterKey);
        } catch (Exception e) {
            log.error(SSE_LOG_PREFIX + "SSE 연결 실패, emitterKey: {}", emitterKey, e);
            emitter.completeWithError(e);
            removeEmitter(favoriteFolderId, emitterKey);
            throw new InternalServerException(ErrorTag.SSE_CONNECTION_ERROR);
        }
    }

    private void sendFolderUpdateEvent(Long favoriteFolderId, ActionType actionType, SseEmitter emitter) {
        try {
            FolderUpdateStreamResponse response = FolderUpdateStreamResponse.of(favoriteFolderId, actionType);

            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(StreamEventType.FOLDER_UPDATE.getName())
                    .data(response);

            emitter.send(event);
        } catch (Exception e) {
            try {
                log.error(SSE_LOG_PREFIX + "폴더 업데이트 이벤트 전송 실패, folderId: {}", favoriteFolderId, e);
                emitter.completeWithError(e);
            } catch (Exception completeException) {
                log.error(SSE_LOG_PREFIX + "completeWithError 실패, folderId: {}", favoriteFolderId, completeException);
            }
        }
    }

    private void removeEmitter(Long favoriteFolderId, String emitterKey) {
        emitters.computeIfPresent(favoriteFolderId, (key, folderEmitters) -> {
            folderEmitters.remove(emitterKey);
            log.info(SSE_LOG_PREFIX + "SSE 연결 해제, emitterKey: {}", emitterKey);
            if (folderEmitters.isEmpty()) {
                return null;
            }
            return folderEmitters;
        });
    }

    private void sendMemberUpdateEvent() {
        // TODO: 구현 필요
    }

    private void startHeartbeat(String emitterKey, SseEmitter emitter) {
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(
                () -> sendHeartbeatEvent(emitter),
                heartbeatInterval,
                heartbeatInterval,
                TimeUnit.SECONDS
        );
        heartbeatSchedules.put(emitterKey, scheduledFuture);
    }

    private void cancelHeartbeat(String emitterKey) {
        ScheduledFuture<?> scheduledFuture = heartbeatSchedules.remove(emitterKey);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    private void sendHeartbeatEvent(SseEmitter emitter) {
        try {
            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(StreamEventType.HEARTBEAT.getName())
                    .data(HeartbeatStreamResponse.create());
            emitter.send(event);
        } catch (Exception e) {
            log.warn(SSE_LOG_PREFIX + "하트비트 전송 실패", e);
            emitter.completeWithError(e);
        }
    }
}
