package turip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.domain.ContentPending;
import turip.content.repository.ContentPendingRepository;

@Service
@RequiredArgsConstructor
public class AdminContentPendingService {

    private final ContentPendingRepository contentPendingRepository;

    @Transactional
    public ContentPending findById(Long id) {
        return contentPendingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_PENDING_NOT_FOUND));
    }
}
