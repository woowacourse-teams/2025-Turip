package turip.controller.dto.request;

import java.util.List;

public record AdminArticleOrderRequest(
        List<AdminArticleOrderItem> orders
) {
}
