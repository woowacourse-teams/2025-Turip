package turip.creator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.creator.controller.dto.response.CreatorResponse;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;

@Service
@RequiredArgsConstructor
public class CreatorService {

    private final CreatorRepository creatorRepository;

    public CreatorResponse getById(Long id) {
        Creator creator = creatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CREATOR_NOT_FOUND));
        return CreatorResponse.from(creator);
    }
}
