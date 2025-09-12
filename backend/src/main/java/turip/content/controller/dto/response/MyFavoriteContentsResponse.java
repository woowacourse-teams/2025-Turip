package turip.content.controller.dto.response;

import java.util.List;
import turip.content.controller.dto.response.todo.ContentWithTripInfoAndFavoriteResponse;

public record MyFavoriteContentsResponse(
        List<ContentWithTripInfoAndFavoriteResponse> contents,
        boolean loadable
) {

    public static MyFavoriteContentsResponse of(List<ContentWithTripInfoAndFavoriteResponse> contents,
                                                boolean loadable) {
        return new MyFavoriteContentsResponse(contents, loadable);
    }
}
