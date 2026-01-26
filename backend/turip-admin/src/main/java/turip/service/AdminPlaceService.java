package turip.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.infrastructure.client.PlaceSearchClient;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchType;

@Service
@RequiredArgsConstructor
public class AdminPlaceService {

    private final List<PlaceSearchClient> searchServices;

    public PlaceSearchResponse searchPlaces(String query, PlaceSearchType type) {
        return searchServices.stream()
                .filter(service -> service.supports(type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorTag.UNSUPPORTED_SEARCH_TYPE))
                .search(query);
    }
}
