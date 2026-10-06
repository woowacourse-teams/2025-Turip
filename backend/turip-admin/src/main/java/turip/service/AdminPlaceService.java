package turip.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.infrastructure.client.PlaceSearchClient;
import turip.place.controller.dto.response.PlaceResponse;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.Place;
import turip.place.domain.PlaceSearchType;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class AdminPlaceService {

    private final List<PlaceSearchClient> searchServices;
    private final PlaceRepository placeRepository;

    public PlaceSearchResponse searchPlaces(String query, PlaceSearchType type) {
        return searchServices.stream()
                .filter(service -> service.supports(type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorTag.UNSUPPORTED_SEARCH_TYPE))
                .search(query);
    }

    public List<PlaceResponse> findPlaces(String query, Integer size) {
        PageRequest pageable = PageRequest.of(0, size);
        List<Place> places = findPlacesByQuery(query, pageable);

        return places.stream()
                .map(PlaceResponse::from)
                .toList();
    }

    private List<Place> findPlacesByQuery(String query, PageRequest pageable) {
        if (query == null || query.isBlank()) {
            return placeRepository.findAllByOrderByIdDesc(pageable);
        }
        String booleanModeKeyword = placeRepository.createBooleanModeKeyword(query);
        return placeRepository.findByNameContaining(booleanModeKeyword, pageable);
    }
}
