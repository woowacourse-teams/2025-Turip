package turip.content.controller.dto.response;

import java.util.List;

public record WeeklyPopularFavoriteContentsResponse(
        List<WeeklyPopularFavoriteContentResponse> contents
) {

    public static WeeklyPopularFavoriteContentsResponse from(List<WeeklyPopularFavoriteContentResponse> contents) {
        return new WeeklyPopularFavoriteContentsResponse(contents);
    }
}
