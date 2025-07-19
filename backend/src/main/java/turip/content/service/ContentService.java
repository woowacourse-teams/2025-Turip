package turip.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.content.repository.ContentRepository;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public int getCountByRegionName(String regionName) {
        return contentRepository.countByRegion_Name(regionName);
    }
}
