package turip.controller.dto.response;

import java.util.List;

public record AdminContentsResponse(
        List<AdminContentResponse> contents,
        boolean loadable
) {
    public static AdminContentsResponse of(List<AdminContentResponse> contents, boolean loadable) {
        return new AdminContentsResponse(contents, loadable);
    }
}
