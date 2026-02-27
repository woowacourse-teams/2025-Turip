package turip.favorite.controller.dto.response;

public record FavoriteContentCountResponse(int count) {

    public static FavoriteContentCountResponse from(int count) {
        return new FavoriteContentCountResponse(count);
    }
}
