package turip.favorite.controller.dto.response;

public record FavoritePlaceCountResponse(int count) {

    public static FavoritePlaceCountResponse from(int count) {
        return new FavoritePlaceCountResponse(count);
    }
}
