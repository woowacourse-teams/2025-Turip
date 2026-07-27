package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("header")
        private Header header;

        @JsonProperty("body")
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {

        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;

        public boolean isSuccess() {
            return "0000".equals(resultCode) || "00".equals(resultCode);
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JsonProperty("items")
        @JsonDeserialize(using = EmptyStringAsNullDeserializer.class)
        private Items items;

        @JsonProperty("numOfRows")
        private Integer numOfRows;

        @JsonProperty("pageNo")
        private Integer pageNo;

        @JsonProperty("totalCount")
        private Integer totalCount;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RelatedSpot {

        @JsonProperty("tAtsCd")
        private String touristSpotCode;

        @JsonProperty("tAtsNm")
        private String touristSpotName;

        @JsonProperty("areaCd")
        private String areaCode;

        @JsonProperty("areaNm")
        private String areaName;

        @JsonProperty("signguCd")
        private String sigunguCode;

        @JsonProperty("signguNm")
        private String sigunguName;

        @JsonProperty("baseYm")
        private String baseYearMonth;

        @JsonProperty("rlteTatsCd")
        private String relatedSpotCode;

        @JsonProperty("rlteTatsNm")
        private String relatedSpotName;

        @JsonProperty("rlteRegnCd")
        private String relatedRegionCode;

        @JsonProperty("rlteRegnNm")
        private String relatedRegionName;

        @JsonProperty("rlteSignguCd")
        private String relatedSigunguCode;

        @JsonProperty("rlteSignguNm")
        private String relatedSigunguName;

        @JsonProperty("rlteCtgryLclsNm")
        private String relatedCategoryLargeName;

        @JsonProperty("rlteCtgryMclsNm")
        private String relatedCategoryMediumName;

        @JsonProperty("rlteCtgrySclsNm")
        private String relatedCategorySmallName;

        @JsonProperty("rlteRank")
        private Integer relatedRank;
    }
}
