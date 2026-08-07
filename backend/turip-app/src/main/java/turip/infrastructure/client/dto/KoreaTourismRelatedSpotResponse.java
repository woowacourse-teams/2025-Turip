package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.infrastructure.client.dto.deserializer.EmptyStringAsNullDeserializer;

/**
 * 한국관광공사 TourAPI의 지역별 연관 관광지 조회 응답
 * API: TarRlteTarService1/areaBasedList1
 */
@Getter
@NoArgsConstructor
public class KoreaTourismRelatedSpotResponse {

    @JsonProperty("response")
    private Response response;

    public List<RelatedSpot> getRelatedSpots() {
        if (response == null || response.body == null || response.body.items == null) {
            return List.of();
        }
        return response.body.items.getItemsOrEmpty();
    }

    public boolean isSuccess() {
        return response != null
                && response.header != null
                && response.header.isSuccess();
    }

    public String getResultCode() {
        return response != null && response.header != null ? response.header.resultCode : null;
    }

    public String getResultMsg() {
        return response != null && response.header != null ? response.header.resultMsg : null;
    }

    @Getter
    @NoArgsConstructor
    public static class Response {

        @JsonProperty("header")
        private Header header;

        @JsonProperty("body")
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {

        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;

        public boolean isSuccess() {
            return "0000".equals(resultCode);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Body {

        @JsonProperty("items")
        @JsonDeserialize(using = EmptyStringAsNullDeserializer.class)
        private Items items;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {

        @JsonProperty("item")
        private List<RelatedSpot> item;

        public List<RelatedSpot> getItemsOrEmpty() {
            return item != null ? item : List.of();
        }
    }

    /**
     * 연관 관광지 정보
     */
    @Getter
    @NoArgsConstructor
    public static class RelatedSpot {

        @JsonProperty("rlteTatsNm")
        private String relatedSpotName;

        @JsonProperty("rlteCtgryLclsNm")
        private String relatedCategoryLargeName;
    }
}
