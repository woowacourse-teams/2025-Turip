package turip.content.controller.dto.response;

import java.util.List;

public record MyFavoriteContentsResponse(
        List<ContentWithTripInfoResponse> contents,
        boolean loadable
) {

    public static MyFavoriteContentsResponse of(List<ContentWithTripInfoResponse> contents, boolean loadable) {
        return new MyFavoriteContentsResponse(contents, loadable);
    }
}
