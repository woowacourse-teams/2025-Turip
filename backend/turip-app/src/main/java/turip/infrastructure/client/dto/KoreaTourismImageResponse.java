package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KoreaTourismImageResponse {

    @JsonProperty("response")
    private Response response;

    public Optional<String> getFirstThumbImageUrl() {
        if (response == null || response.body == null || response.body.items == null
                || response.body.items.item == null || response.body.items.item.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.body.items.item.get(0).thumbImage);
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
        private Items items;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {
        @JsonProperty("item")
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    public static class Item {
        @JsonProperty("thumbImage")
        private String thumbImage;
    }
}
