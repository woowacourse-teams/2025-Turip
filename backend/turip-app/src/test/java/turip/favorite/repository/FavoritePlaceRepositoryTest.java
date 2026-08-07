package turip.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.dto.FavoriteFolderItemCountResult;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;
import turip.util.fixture.FavoriteFolderFixture;
import turip.util.fixture.FavoritePlaceFixture;
import turip.util.fixture.PlaceFixture;

@DataJpaTest
@ActiveProfiles({"test", "h2"})
class FavoritePlaceRepositoryTest {

    @Autowired
    private FavoriteFolderRepository favoriteFolderRepository;

    @Autowired
    private FavoritePlaceRepository favoritePlaceRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Nested
    class CountByFavoriteFolderIdsIn {

        @DisplayName("폴더들의 각 장소찜 수 반환할 수 있다")
        @Test
        void countByFavoriteFolderIdsIn() {
            // given
            FavoriteFolder folder1 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더1"));
            FavoriteFolder folder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더2"));
            FavoriteFolder folder3 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더3"));
            FavoriteFolder folder4 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더4"));
            List<Long> folderIds = List.of(folder1.getId(), folder2.getId(), folder3.getId(), folder4.getId());

            // Place 먼저 저장
            Place place1 = placeRepository.save(PlaceFixture.create());
            Place place2 = placeRepository.save(PlaceFixture.create());
            Place place3 = placeRepository.save(PlaceFixture.create());
            Place place4 = placeRepository.save(PlaceFixture.create());
            Place place5 = placeRepository.save(PlaceFixture.create());
            Place place6 = placeRepository.save(PlaceFixture.create());

            // folder 1에 장소찜 1개
            favoritePlaceRepository.save(FavoritePlaceFixture.create(folder1, place1));

            // folder 2에 장소찜 2개
            favoritePlaceRepository.save(FavoritePlaceFixture.create(folder2, place2));
            favoritePlaceRepository.save(FavoritePlaceFixture.create(folder2, place3));

            // folder 3에 장소찜 3개
            favoritePlaceRepository.save(FavoritePlaceFixture.create(folder3, place4));
            favoritePlaceRepository.save(FavoritePlaceFixture.create(folder3, place5));
            favoritePlaceRepository.save(FavoritePlaceFixture.create(folder3, place6));

            // when
            List<FavoriteFolderItemCountResult> results = favoritePlaceRepository.countByFavoriteFolderIdsIn(folderIds);

            // then
            assertThat(results).containsAll(List.of(
                    new FavoriteFolderItemCountResult(folder1.getId(), 1L),
                    new FavoriteFolderItemCountResult(folder2.getId(), 2L),
                    new FavoriteFolderItemCountResult(folder3.getId(), 3L),
                    new FavoriteFolderItemCountResult(folder4.getId(), 0L)
            ));
        }
    }
}
