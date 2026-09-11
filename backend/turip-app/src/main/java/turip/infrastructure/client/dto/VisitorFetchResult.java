package turip.infrastructure.client.dto;

import java.util.List;
import turip.infrastructure.client.dto.KoreaTourismVisitorResponse.VisitorItem;

public record VisitorFetchResult(
        boolean isSuccess,
        List<VisitorItem> items
) {
    public static VisitorFetchResult success(List<VisitorItem> items) {
        return new VisitorFetchResult(true, items);
    }

    public static VisitorFetchResult failure() {
        return new VisitorFetchResult(false, List.of());
    }
}
