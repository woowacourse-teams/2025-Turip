package turip.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentCountResponse countByRegionName(String regionName) {
        int count = contentRepository.countByRegion_Name(regionName);
        return ContentCountResponse.from(count);
    }

    private Content getById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
    }
}
