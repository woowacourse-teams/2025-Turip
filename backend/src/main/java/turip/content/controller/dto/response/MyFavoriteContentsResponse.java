package turip.content.controller.dto.response;

import java.util.List;
import turip.content.controller.dto.response.todo.ContentDetailsResponse;

public record MyFavoriteContentsResponse(
        List<ContentDetailsResponse> contents,
        boolean loadable
) {

    public static MyFavoriteContentsResponse of(List<ContentDetailsResponse> contents,
                                                boolean loadable) {
        return new MyFavoriteContentsResponse(contents, loadable);
    }
}
