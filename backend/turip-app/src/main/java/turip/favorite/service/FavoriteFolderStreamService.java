package turip.favorite.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import turip.account.domain.Member;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.InternalServerException;
import turip.favorite.controller.dto.response.sse.ConnectStreamResponse;
import turip.favorite.controller.dto.response.sse.FolderUpdateStreamResponse;
import turip.favorite.repository.FavoriteFolderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteFolderStreamService {

    private static final Long DEFAULT_TIMEOUT = 3 * 60 * 1000L; // 3분
    private static final String SSE_LOG_PREFIX = "[SSE] ";
    private final Map<Long, Map<Long, SseEmitter>> emitters = new ConcurrentHashMap<>();

    private final FavoriteFolderRepository favoriteFolderRepository;

    public SseEmitter createEmitter(Long favoriteFolderId, Member member) {
        validateIfMemberJoiningFavoriteFolder(favoriteFolderId, member);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // emitter 제거 콜백 메서드 등록
        emitter.onCompletion(() -> {
            removeEmitter(favoriteFolderId, member.getId());
            log.info(SSE_LOG_PREFIX + "클라이언트 연결 해제, folderId: {}, memberId: {}",
                    favoriteFolderId, member.getId());
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            log.info(SSE_LOG_PREFIX + "연결 타임아웃, folderId: {}, memberId: {}",
                    favoriteFolderId, member.getId());
        });

        emitter.onError(e -> {
            removeEmitter(favoriteFolderId, member.getId());
            log.error(SSE_LOG_PREFIX + "연결 에러, folderId: {}, memberId: {}",
                    favoriteFolderId, member.getId(), e);
        });

        validateIfMemberJoiningFavoriteFolder(favoriteFolderId, member);

        emitters.computeIfAbsent(favoriteFolderId, key -> new ConcurrentHashMap<>())
                .put(member.getId(), emitter);

        sendConnectEvent(favoriteFolderId, emitter);
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
                    .name(SseEventType.FOLDER_UPDATE.getName())
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

    private void sendConnectEvent(Long favoriteFolderId, SseEmitter emitter) {
        try {
            ConnectStreamResponse response = ConnectStreamResponse.from(favoriteFolderId);

            SseEventBuilder event = SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(SseEventType.CONNECT.getName())
                    .data(response);

            emitter.send(event);
            log.info(SSE_LOG_PREFIX + "SSE 연결 성공, folderId: {}", favoriteFolderId);
        } catch (IOException e) {
            log.error(SSE_LOG_PREFIX + "SSE 연결 실패, folderId: {}", favoriteFolderId, e);
            throw new InternalServerException(ErrorTag.SSE_CONNECTION_ERROR);
        }
    }

    private void sendMemberUpdateEvent() {
        // TODO: 구현 필요
    }
}
