package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KoreaTourismResponse {

    @JsonProperty("response")
    private Response response;

    public Optional<String> getFirstThumbImageUrl() {
        if (response == null || response.body == null || response.body.items == null
                || response.body.items.item == null || response.body.items.item.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.body.items.item.get(0).thumbImage);
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("body")
        private Body body;
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
