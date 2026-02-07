package turip.favorite.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import turip.account.domain.Member;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.InternalServerException;
import turip.favorite.controller.dto.response.ConnectStreamResponse;
import turip.favorite.controller.dto.response.FolderUpdateStreamResponse;
import turip.favorite.controller.dto.response.HeartbeatStreamResponse;
import turip.favorite.repository.FavoriteFolderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteFolderStreamService {

    private static final Long DEFAULT_TIMEOUT = 3 * 60 * 1000L; // 3분
    private static final Long HEARTBEAT_INTERVAL = 30L; // 30초
    private static final String SSE_LOG_PREFIX = "[SSE] ";
    private final Map<Long, Map<Long, SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> heartbeatSchedules = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final FavoriteFolderRepository favoriteFolderRepository;

    public SseEmitter createEmitter(Long favoriteFolderId, Member member) {
        validateIfMemberJoiningFavoriteFolder(favoriteFolderId, member);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        Long emitterKey = member.getId();

        emitter.onCompletion(() -> {
            cancelHeartbeat(emitterKey);
            removeEmitter(favoriteFolderId, emitterKey);
            log.info(SSE_LOG_PREFIX + "클라이언트 연결 해제, folderId: {}, memberId: {}",
                    favoriteFolderId, emitterKey);
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            log.info(SSE_LOG_PREFIX + "연결 타임아웃, folderId: {}, memberId: {}",
                    favoriteFolderId, emitterKey);
        });

        emitter.onError(e -> {
            cancelHeartbeat(emitterKey);
            removeEmitter(favoriteFolderId, emitterKey);
            log.error(SSE_LOG_PREFIX + "연결 에러, folderId: {}, memberId: {}",
                    favoriteFolderId, emitterKey, e);
        });

        emitters.computeIfAbsent(favoriteFolderId, key -> new ConcurrentHashMap<>())
                .put(emitterKey, emitter);

        sendConnectEvent(favoriteFolderId, emitter, emitterKey);
        return emitter;
    }

    public void sendFolderUpdateEvents(Long favoriteFolderId, ActionType actionType) {
        Map<Long, SseEmitter> folderEmitters = emitters.get(favoriteFolderId);
        if (folderEmitters == null || folderEmitters.isEmpty()) {
            log.info(SSE_LOG_PREFIX + "폴더에 연결된 사용자 없음, folderId: {}", favoriteFolderId);
            return;
        }
        log.info(SSE_LOG_PREFIX + "폴더 업데이트 이벤트 전송 시작, folderId: {}, 연결된 사용자 수: {}", favoriteFolderId,
                folderEmitters.size());
        folderEmitters.values()
                .forEach(emitter -> sendFolderUpdateEvent(favoriteFolderId, actionType, emitter));
    }

    private void sendFolderUpdateEvent(Long favoriteFolderId, ActionType actionType, SseEmitter emitter) {
        try {
            FolderUpdateStreamResponse response = FolderUpdateStreamResponse.of(favoriteFolderId, actionType);

            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(StreamEventType.FOLDER_UPDATE.getName())
                    .data(response);

            emitter.send(event);
        } catch (IOException e) {
            log.error(SSE_LOG_PREFIX + "폴더 업데이트 이벤트 전송 실패, folderId: {}", favoriteFolderId, e);
            emitter.completeWithError(e);
        }
    }

    private void removeEmitter(Long favoriteFolderId, Long memberId) {
        Map<Long, SseEmitter> turipEmitters = emitters.get(favoriteFolderId);
        if (turipEmitters != null) {
            turipEmitters.remove(memberId);
            log.info(SSE_LOG_PREFIX + "SSE 연결 해제, favoriteFolderId: {}, memberId: {}", favoriteFolderId, memberId);

            if (turipEmitters.isEmpty()) {
                emitters.remove(favoriteFolderId);
            }
        }
    }

    private void validateIfMemberJoiningFavoriteFolder(Long favoriteFolderId, Member member) {
        if (!favoriteFolderRepository.existsByIdAndAccount(favoriteFolderId, member.getAccount())) {
            throw new ForbiddenException(ErrorTag.FOLDER_STREAM_FORBIDDEN);
        }
    }

    private void sendConnectEvent(Long favoriteFolderId, SseEmitter emitter, Long emitterKey) {
        try {
            ConnectStreamResponse response = ConnectStreamResponse.from(favoriteFolderId);

            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(StreamEventType.CONNECT.getName())
                    .data(response);

            emitter.send(event);
            startHeartbeat(emitterKey, emitter);
            log.info(SSE_LOG_PREFIX + "SSE 연결 성공, folderId: {}", favoriteFolderId);
        } catch (IOException e) {
            log.error(SSE_LOG_PREFIX + "SSE 연결 실패, folderId: {}", favoriteFolderId, e);
            throw new InternalServerException(ErrorTag.SSE_CONNECTION_ERROR);
        }
    }

    private void sendMemberUpdateEvent() {
        // TODO: 구현 필요
    }

    private void startHeartbeat(Long emitterKey, SseEmitter emitter) {
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(
                () -> sendHeartbeat(emitter),
                HEARTBEAT_INTERVAL,
                HEARTBEAT_INTERVAL,
                TimeUnit.SECONDS
        );
        heartbeatSchedules.put(emitterKey, scheduledFuture);
    }

    private void cancelHeartbeat(Long emitterKey) {
        ScheduledFuture<?> scheduledFuture = heartbeatSchedules.remove(emitterKey);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    private void sendHeartbeat(SseEmitter emitter) {
        try {
            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(StreamEventType.HEARTBEAT.getName())
                    .data(HeartbeatStreamResponse.create());
            emitter.send(event);
        } catch (IOException e) {
            log.warn(SSE_LOG_PREFIX + "하트비트 전송 실패", e);
            emitter.completeWithError(e);
        }
    }
}
