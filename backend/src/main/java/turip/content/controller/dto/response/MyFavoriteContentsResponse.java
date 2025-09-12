package turip.content.controller.dto.response;

import java.util.List;
import turip.content.controller.dto.response.todo.ContentWithTripInfo;

public record MyFavoriteContentsResponse(
        List<ContentWithTripInfo> contents,
        boolean loadable
) {

    public static MyFavoriteContentsResponse of(List<ContentWithTripInfo> contents,
                                                boolean loadable) {
        return new MyFavoriteContentsResponse(contents, loadable);
    }
}
