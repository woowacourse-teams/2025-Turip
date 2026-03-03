package turip.util.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.place.domain.Place;

public class FavoritePlaceFixture {

    public static FavoritePlace create(FavoriteFolder favoriteFolder, Place place) {
        return new FavoritePlace(favoriteFolder, place, 0);
    }

    public static FavoritePlace createWithId(Long id, FavoriteFolder favoriteFolder, Place place) {
        FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, 0);
        ReflectionTestUtils.setField(favoritePlace, "id", id);
        return favoritePlace;
    }

    public static FavoritePlace createWithOrder(FavoriteFolder favoriteFolder, Place place, Integer favoriteOrder) {
        return new FavoritePlace(favoriteFolder, place, favoriteOrder);
    }

    public static FavoritePlace createWithIdAndOrder(Long id, FavoriteFolder favoriteFolder, Place place,
                                                     Integer favoriteOrder) {
        FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, favoriteOrder);
        ReflectionTestUtils.setField(favoritePlace, "id", id);
        return favoritePlace;
    }
}
