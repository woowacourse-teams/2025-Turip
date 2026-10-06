package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.infrastructure.client.dto.deserializer.EmptyStringAsNullVisitorItemsDeserializer;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoreaTourismVisitorResponse {

    @JsonProperty("response")
    private Response response;

    public List<VisitorItem> getVisitorItems() {
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

    public Integer getTotalCount() {
        if (response == null || response.body == null) {
            return null;
        }
        return response.body.totalCount;
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
            return "0000".equals(resultCode);
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JsonProperty("items")
        @JsonDeserialize(using = EmptyStringAsNullVisitorItemsDeserializer.class)
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
        private List<VisitorItem> item;

        public List<VisitorItem> getItemsOrEmpty() {
            return item != null ? item : List.of();
        }
    }

    /**
     * 지역별 방문자 수 항목 (일별) touDivCd - 관광객 구분 코드 (1:현지인, 2:외지인, 3:외국인) touNum   - 관광객 수 (소수점 포함)
     */
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VisitorItem {

        @JsonProperty("areaCode")
        private String areaCode;

        @JsonProperty("areaNm")
        private String areaName;

        @JsonProperty("signguCode")
        private String signguCode;

        @JsonProperty("signguNm")
        private String signguName;

        @JsonProperty("touDivCd")
        private String touristDivisionCode;

        @JsonProperty("touDivNm")
        private String touristDivisionName;

        @JsonProperty("touNum")
        private Double touristCount;

        @JsonProperty("baseYmd")
        private String baseYmd;
    }
}
