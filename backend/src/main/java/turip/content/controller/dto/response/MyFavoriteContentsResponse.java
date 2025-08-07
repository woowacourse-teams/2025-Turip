package turip.content.controller.dto.response;

import java.util.List;

public record MyFavoriteContentsResponse(
        List<ContentWithTripInfoAndFavoriteResponse> contents,
        boolean loadable
) {

    public static MyFavoriteContentsResponse of(List<ContentWithTripInfoAndFavoriteResponse> contents,
                                                boolean loadable) {
        return new MyFavoriteContentsResponse(contents, loadable);
    }
}
