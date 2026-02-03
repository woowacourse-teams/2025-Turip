package turip.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoritePlaceOrderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlacesWithPlaceDetailResponse;
import turip.favorite.controller.dto.response.FavoritePlaceCountResponse;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderFixture;

@ExtendWith(MockitoExtension.class)
class FavoritePlaceServiceTest {

    @InjectMocks
    private FavoritePlaceService favoritePlaceService;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private FavoriteFolderAccountService favoriteFolderAccountService;

    @DisplayName("장소 찜 생성 테스트")
    @Nested
    class Create {

        @DisplayName("장소 찜을 생성할 수 있다")
        @Test
        void create1() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(1L, "폴더1");
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);
            FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(false);
            given(favoritePlaceRepository.save(favoritePlace))
                    .willReturn(new FavoritePlace(1L, favoriteFolder, place, 1));

            // when
            FavoritePlaceResponse response = favoritePlaceService.create(account, favoriteFolderId, placeId);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(favoriteFolderId),
                    () -> assertThat(response.favoriteFolderId()).isEqualTo(favoriteFolderId),
                    () -> assertThat(response.placeId()).isEqualTo(placeId)
            );
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 ForbiddenException을 발생시킨다")
        @Test
        void create2() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Account requestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(favoriteFolderId, "폴더1");
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            doThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .when(favoriteFolderAccountService)
                    .validateOwnership(requestAccount, favoriteFolder);

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(requestAccount, favoriteFolderId, placeId))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void create3() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;
            Account account = AccountFixture.createUser();

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(account, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void create4() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolder();

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(account, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.PLACE_NOT_FOUND.getMessage());
        }

        @DisplayName("해당 폴더에 장소 찜이 되어있는 경우 ConflictException을 발생시킨다")
        @Test
        void create5() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolder();
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(account, favoriteFolderId, placeId))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @DisplayName("특정 폴더의 장소 찜 조회 테스트")
    @Nested
    class FindAllByFolder {

        @DisplayName("특정 폴더의 장소 찜 폴더를 조회할 수 있다")
        @Test
        void findAllByFolder1() {
            // given
            Long favoriteFolderId = 1L;
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolder();
            Place place1 = new Place(1L, "장소1", "url1", "주소1", 1, 1);
            Place place2 = new Place(2L, "장소2", "url2", "주소2", 2, 2);
            FavoritePlace favoritePlace1 = new FavoritePlace(1L, favoriteFolder, place1, 1);
            FavoritePlace favoritePlace2 = new FavoritePlace(2L, favoriteFolder, place2, 2);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoritePlaceRepository.findAllByFavoriteFolderOrderByFavoriteOrderAsc(favoriteFolder))
                    .willReturn(List.of(favoritePlace1, favoritePlace2));

            // when
            FavoritePlacesWithPlaceDetailResponse response = favoritePlaceService.findAllByFolder(
                    favoriteFolderId);

            // then
            assertAll(
                    () -> assertThat(response.favoritePlaceCount()).isEqualTo(2),
                    () -> assertThat(response.favoritePlaces().get(0).id()).isEqualTo(1L),
                    () -> assertThat(response.favoritePlaces().get(1).id()).isEqualTo(2L)
            );
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void findAllByFolder2() {
            // given
            Long favoriteFolderId = 1L;
            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.findAllByFolder(favoriteFolderId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }
    }

    @DisplayName("계정의 장소 찜 수 조회 테스트")
    @Nested
    class CountByAccount {

        @DisplayName("특정 계정의 장소 찜 개수를 조회할 수 있다")
        @Test
        void countByAccount1() {
            // given
            Account account = AccountFixture.createUser();
            int favoritePlaceCount = 3;
            given(favoritePlaceRepository.countByAccount(account))
                    .willReturn(favoritePlaceCount);

            // when
            FavoritePlaceCountResponse response = favoritePlaceService.countByAccount(account);

            // then
            assertThat(response.count())
                    .isEqualTo(favoritePlaceCount);
        }
    }

    @DisplayName("장소 찜 순서 변경 테스트")
    @Nested
    class UpdatePlaceOrder {

        @DisplayName("장소 찜 순서를 변경할 수 있다")
        @Test
        void updatePlaceOrder1() {
            // given
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolder();
            FavoritePlace firstFavoritePlace = new FavoritePlace(1L, favoriteFolder, null, 1);
            FavoritePlace secondFavoritePlace = new FavoritePlace(2L, favoriteFolder, null, 2);
            given(favoriteFolderRepository.findById(favoriteFolder.getId()))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoritePlaceRepository.findById(firstFavoritePlace.getId()))
                    .willReturn(Optional.of(firstFavoritePlace));
            given(favoritePlaceRepository.findById(secondFavoritePlace.getId()))
                    .willReturn(Optional.of(secondFavoritePlace));

            // when
            FavoritePlaceOrderRequest request = new FavoritePlaceOrderRequest(List.of(2L, 1L));
            favoritePlaceService.updatePlaceOrder(account, favoriteFolder.getId(), request);

            // then
            assertAll(
                    () -> assertThat(firstFavoritePlace.getFavoriteOrder()).isEqualTo(2),
                    () -> assertThat(secondFavoritePlace.getFavoriteOrder()).isEqualTo(1)
            );
        }

        @DisplayName("변경하려는 장소 찜의 폴더가 요청 계정의 소유가 아닌 경우 예외를 발생시킨다")
        @Test
        void updatePlaceOrder2() {
            // given
            Account requestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolder();

            given(favoriteFolderRepository.findById(favoriteFolder.getId()))
                    .willReturn(Optional.of(favoriteFolder));
            doThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .when(favoriteFolderAccountService)
                    .validateOwnership(requestAccount, favoriteFolder);

            // when
            FavoritePlaceOrderRequest request = new FavoritePlaceOrderRequest(List.of(2L, 1L));

            // then
            assertThatThrownBy(
                    () -> favoritePlaceService.updatePlaceOrder(requestAccount, favoriteFolder.getId(), request))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("변경하려는 장소 찜이 다른 장소 찜 폴더의 장소 찜인 경우 예외를 발생시킨다")
        @Test
        void updatePlaceOrder3() {
            // given
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(1L, "폴더1");
            FavoriteFolder otherFavoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(2L, "다른 폴더");
            FavoritePlace firstFavoritePlace = new FavoritePlace(1L, favoriteFolder, null, 1);
            FavoritePlace secondFavoritePlace = new FavoritePlace(2L, otherFavoriteFolder, null, 2);
            given(favoriteFolderRepository.findById(favoriteFolder.getId()))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoritePlaceRepository.findById(firstFavoritePlace.getId()))
                    .willReturn(Optional.of(firstFavoritePlace));
            given(favoritePlaceRepository.findById(secondFavoritePlace.getId()))
                    .willReturn(Optional.of(secondFavoritePlace));

            // when
            FavoritePlaceOrderRequest request = new FavoritePlaceOrderRequest(List.of(1L, 2L));

            // then
            assertThatThrownBy(
                    () -> favoritePlaceService.updatePlaceOrder(account, favoriteFolder.getId(), request))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @DisplayName("장소 찜 삭제 테스트")
    @Nested
    class Remove {

        @DisplayName("장소 찜을 삭제할 수 있다")
        @Test
        void remove1() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(favoriteFolderId, "폴더1");
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);
            FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(Optional.of(favoritePlace));

            // when & then
            assertDoesNotThrow(() -> favoritePlaceService.remove(account, favoriteFolderId, placeId));
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 ForbiddenException을 발생시킨다")
        @Test
        void remove2() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Account requestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(1L, "폴더1");
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            doThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .when(favoriteFolderAccountService)
                    .validateOwnership(requestAccount, favoriteFolder);

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(requestAccount, favoriteFolderId, placeId))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove3() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;
            Account account = AccountFixture.createUser();

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(account, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove4() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(favoriteFolderId, "폴더1");

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(account, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.PLACE_NOT_FOUND.getMessage());
        }

        @DisplayName("삭제하려는 장소 찜이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove5() {
            // given
            Long favoriteFolderId = 1L;
            Long placeId = 1L;
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(favoriteFolderId, "폴더1");
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(account, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_PLACE_NOT_FOUND.getMessage());
        }
    }
}
