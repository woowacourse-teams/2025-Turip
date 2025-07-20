package turip.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.repository.ContentRepository;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentCountResponse getCountByRegionName(String regionName) {
        int count = contentRepository.countByRegion_Name(regionName);
        return ContentCountResponse.from(count);
    }
}
