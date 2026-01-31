package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchProvider;

@Getter
@NoArgsConstructor
public class KakaoPlaceSearchResponse {

    @JsonProperty("documents")
    private List<Document> documents;

    public PlaceSearchResponse toPlaceSearchResponse() {
        if (documents == null) {
            return new PlaceSearchResponse(PlaceSearchProvider.KAKAO, Collections.emptyList());
        }
        List<PlaceSearchResponse.PlaceSearchItem> items = documents.stream()
                .map(doc -> new PlaceSearchResponse.PlaceSearchItem(
                        doc.placeName,
                        doc.placeUrl,
                        doc.addressName,
                        Double.parseDouble(doc.y),
                        Double.parseDouble(doc.x),
                        doc.categoryName))
                .toList();
        return new PlaceSearchResponse(PlaceSearchProvider.KAKAO, items);
    }

    @Getter
    @NoArgsConstructor
    public static class Document {
        @JsonProperty("id")
        private String id;
        @JsonProperty("place_name")
        private String placeName;
        @JsonProperty("category_name")
        private String categoryName;
        @JsonProperty("address_name")
        private String addressName;
        @JsonProperty("road_address_name")
        private String roadAddressName;
        @JsonProperty("x")
        private String x;
        @JsonProperty("y")
        private String y;
        @JsonProperty("place_url")
        private String placeUrl;
    }
}
