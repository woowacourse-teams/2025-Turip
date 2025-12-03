package turip.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.account.domain.Account;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteFolderServiceTest {

    @InjectMocks
    private FavoriteFolderService favoriteFolderService;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @Mock
    private PlaceRepository placeRepository;

    @DisplayName("기본 장소 찜 폴더 생성 테스트")
    @Nested
    class CreateDefaultFavoriteFolder {

        @DisplayName("기본 찜 폴더를 생성할 수 있다")
        @Test
        void createDefaultFavoriteFolder() {
            // given
            Long accountId = 1L;
            Account account = new Account(accountId);

            // when
            favoriteFolderService.createDefaultFavoriteFolder(account);

            // then
            verify(favoriteFolderRepository).save(FavoriteFolder.defaultFolderOf(account));
        }
    }

    @DisplayName("커스텀 장소 찜 폴더 생성 테스트")
    @Nested
    class CreateCustomFavoriteFolder {

        @DisplayName("커스텀 찜 폴더를 생성할 수 있다")
        @Test
        void createCustomFavoriteFolder1() {
            // given
            String folderName = "괜찮은 소품샵 모음";
            Long accountId = 1L;
            boolean isDefault = false;
            Long folderId = 1L;

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Account account = new Account(accountId);

            given(favoriteFolderRepository.existsByNameAndAccount(folderName, account))
                    .willReturn(false);
            given(favoriteFolderRepository.save(FavoriteFolder.customFolderOf(account, folderName)))
                    .willReturn(new FavoriteFolder(folderId, account, folderName, isDefault));

            // when
            FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, account);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(folderId),
                    () -> assertThat(response.name()).isEqualTo(folderName),
                    () -> assertThat(response.memberId()).isEqualTo(accountId),
                    () -> assertThat(response.isDefault()).isFalse()
            );
        }

        @DisplayName("해당 회원이 이미 같은 이름의 폴더를 소유하고 있는 경우 ConflictException을 발생시킨다")
        @Test
        void createCustomFavoriteFolder2() {
            // given
            String folderName = "괜찮은 소품샵 모음";
            Long accountId = 1L;

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Account account = new Account(accountId);

            given(favoriteFolderRepository.existsByNameAndAccount(folderName, account))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.createCustomFavoriteFolder(request, account))
                    .isInstanceOf(ConflictException.class);
        }

        @DisplayName("폴더 이름이 형식에 맞지 않는 경우 IllegalArgumentException이 발생한다")
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "21글자폴더입니다용21글자폴더입니다용~"})
        void createCustomFavoriteFolder3(String folderName) {
            // given
            Long accountId = 1L;

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Account member = new Account(accountId);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.createCustomFavoriteFolder(request, member))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("특정 회원의 장소 찜 폴더 목록 조회 테스트")
    @Nested
    class FindAllByDeviceFid {

        @DisplayName("기기 id에 대한 찜 폴더 목록을 조회할 수 있다")
        @Test
        void findAllByDeviceFid1() {
            // given
            Account savedAccount = new Account(1L);

            FavoriteFolder defaultFolder = new FavoriteFolder(1L, savedAccount, "기본 폴더", true);
            FavoriteFolder favoriteFolder = new FavoriteFolder(2L, savedAccount, "커스텀 폴더 1", true);
            given(favoriteFolderRepository.findAllByAccountOrderByIdAsc(savedAccount))
                    .willReturn(List.of(defaultFolder, favoriteFolder));

            int defaultFolderPlaceCount = 3;
            int favoriteFolderPlaceCount = 4;
            given(favoritePlaceRepository.countByFavoriteFolder(defaultFolder))
                    .willReturn(defaultFolderPlaceCount);
            given(favoritePlaceRepository.countByFavoriteFolder(favoriteFolder))
                    .willReturn(favoriteFolderPlaceCount);

            // when
            FavoriteFoldersWithPlaceCountResponse response = favoriteFolderService.findAllByMember(savedAccount);

            // then
            assertAll(
                    () -> assertThat(response.favoriteFolders().get(0).placeCount()).isEqualTo(defaultFolderPlaceCount),
                    () -> assertThat(response.favoriteFolders().get(0).name()).isEqualTo("기본 폴더"),
                    () -> assertThat(response.favoriteFolders().get(1).placeCount()).isEqualTo(
                            favoriteFolderPlaceCount),
                    () -> assertThat(response.favoriteFolders().get(1).name()).isEqualTo("커스텀 폴더 1")
            );
        }
    }

    @DisplayName("특정 회원의 장소 찜 폴더 목록과 장소 찜 여부 조회 테스트")
    @Nested
    class FindAllWithFavoriteStatusByDeviceId {

        @DisplayName("기기 id에 대한 찜 폴더 목록과 찜 여부를 조회할 수 있다")
        @Test
        void findAllWithFavoriteStatusByDeviceId1() {
            // given
            Account savedAccount = new Account(1L);

            FavoriteFolder defaultFolder = new FavoriteFolder(1L, savedAccount, "기본 폴더", true);
            FavoriteFolder favoriteFolder = new FavoriteFolder(2L, savedAccount, "커스텀 폴더 1", false);
            given(favoriteFolderRepository.findAllByAccountOrderByIdAsc(savedAccount))
                    .willReturn(List.of(defaultFolder, favoriteFolder));

            Long placeId = 1L;
            Place place = new Place(placeId, "장소", "url", "주소", 1, 1);
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.findFavoriteFolderIdsByPlaceAndFavoriteFolderIn(place,
                    List.of(defaultFolder, favoriteFolder)))
                    .willReturn(Set.of(1L));

            // when
            FavoriteFoldersWithFavoriteStatusResponse response = favoriteFolderService.findAllWithFavoriteStatusByAccountId(
                    savedAccount, placeId);

            // then
            assertAll(
                    () -> assertThat(response.favoriteFolders().get(0).isFavoritePlace()).isTrue(),
                    () -> assertThat(response.favoriteFolders().get(1).isFavoritePlace()).isFalse()
            );
        }

        @DisplayName("placeId에 대한 장소를 찾지 못한 경우 NotFoundException을 발생시킨다")
        @Test
        void findAllWithFavoriteStatusByDeviceId2() {
            // given
            Account savedAccount = new Account(1L);

            Long placeId = 1L;
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> favoriteFolderService.findAllWithFavoriteStatusByAccountId(savedAccount, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.PLACE_NOT_FOUND.getMessage());
        }
    }

    @DisplayName("장소 찜 폴더 이름 변경 테스트")
    @Nested
    class UpdateName {

        @DisplayName("찜 폴더의 이름을 변경할 수 있다")
        @Test
        void updateName1() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "변경된 폴더 이름";
            boolean isDefault = false;

            Account account = new Account(accountId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, account, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when
            FavoriteFolderResponse response = favoriteFolderService.updateName(account, folderId, request);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(folderId),
                    () -> assertThat(response.name()).isEqualTo(newName),
                    () -> assertThat(response.memberId()).isEqualTo(accountId),
                    () -> assertThat(response.isDefault()).isFalse()
            );
        }

        @DisplayName("favoriteFolderId에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void updateName3() {
            // given
            Long accountId = 1L;
            Long nonExistentFolderId = 999L;
            String newName = "변경된 폴더 이름";

            Account member = new Account(accountId);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(nonExistentFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(member, nonExistentFolderId, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("요청 회원 정보와 폴더 소유자의 정보가 일치하지 않는 경우 ForbiddenException을 발생시킨다")
        @Test
        void updateName4() {
            // given
            Long requestMemberId = 1L;
            Long ownerMemberId = 2L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "변경된 폴더 이름";
            boolean isDefault = false;

            Account requestAccount = new Account(requestMemberId);
            Account ownerAccount = new Account(ownerMemberId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, ownerAccount, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(requestAccount, folderId, request))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("해당 회원이 이미 같은 이름의 폴더를 소유하고 있는 경우 ConflictException을 발생시킨다")
        @Test
        void updateName5() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "중복된 폴더 이름";
            boolean isDefault = false;

            Account account = new Account(accountId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, account, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoriteFolderRepository.existsByNameAndAccount(newName, account))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(account, folderId, request))
                    .isInstanceOf(ConflictException.class);
        }

        @DisplayName("폴더 이름이 형식에 맞지 않는 경우 IllegalArgumentException이 발생한다")
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "21글자폴더입니다용21글자폴더입니다용~"})
        void updateName6(String newName) {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            boolean isDefault = false;

            Account member = new Account(accountId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, member, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(member, folderId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("기본 찜 폴더를 수정하려는 경우 BadRequestException을 발생시킨다")
        @Test
        void updateName7() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String oldName = "기본 폴더";
            String newName = "새로운 폴더 이름";
            boolean isDefault = true;

            Account member = new Account(accountId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, member, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(member, folderId, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }
    }

    @DisplayName("장소 찜 폴더 삭제 테스트")
    @Nested
    class Remove {

        @DisplayName("장소 찜 폴더를 삭제할 수 있다")
        @Test
        void remove1() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String folderName = "삭제할 폴더";
            boolean isDefault = false;

            Account member = new Account(accountId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, member, folderName, isDefault);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when
            favoriteFolderService.remove(member, folderId);

            // then
            verify(favoritePlaceRepository).deleteAllByFavoriteFolder(favoriteFolder);
            verify(favoriteFolderRepository).deleteById(folderId);
        }

        @DisplayName("favoriteFolderId에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove3() {
            // given
            Long accountId = 1L;
            Long nonExistentFolderId = 999L;

            Account member = new Account(accountId);

            given(favoriteFolderRepository.findById(nonExistentFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.remove(member, nonExistentFolderId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("요청 회원 정보와 폴더 소유자의 정보가 일치하지 않는 경우 ForbiddenException을 발생시킨다")
        @Test
        void remove4() {
            // given
            Long requestAccountId = 1L;
            Long ownerMemberId = 2L;
            Long folderId = 1L;
            String folderName = "다른 사람의 폴더";
            boolean isDefault = false;

            Account requestAccount = new Account(requestAccountId);
            Account ownerAccount = new Account(ownerMemberId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, ownerAccount, folderName, isDefault);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.remove(requestAccount, folderId))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("기본 찜 폴더를 삭제하려는 경우 BadRequestException을 발생시킨다")
        @Test
        void remove5() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String folderName = "기본 폴더";
            boolean isDefault = true;

            Account member = new Account(accountId);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, member, folderName, isDefault);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.remove(member, folderId))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }
    }

    @DisplayName("Account 기준 장소 찜 폴더 일괄 삭제 테스트")
    @Nested
    class RemoveByAccount {

        @DisplayName("Account의 모든 장소 찜 폴더와 폴더 내 장소 찜들을 삭제할 수 있다")
        @Test
        void removeByAccount1() {
            // given
            Long accountId = 1L;
            Account account = new Account(accountId);

            FavoriteFolder defaultFolder = new FavoriteFolder(1L, account, "기본 폴더", true);
            FavoriteFolder customFolder = new FavoriteFolder(2L, account, "커스텀 폴더", false);

            given(favoriteFolderRepository.findAllByAccount(account))
                    .willReturn(List.of(defaultFolder, customFolder));

            // when
            favoriteFolderService.removeByAccount(account);

            // then
            verify(favoritePlaceRepository).deleteAllByFavoriteFolder(defaultFolder);
            verify(favoritePlaceRepository).deleteAllByFavoriteFolder(customFolder);
            verify(favoriteFolderRepository).delete(defaultFolder);
            verify(favoriteFolderRepository).delete(customFolder);
        }

        @DisplayName("Account에 폴더가 없는 경우에도 정상적으로 처리된다")
        @Test
        void removeByAccount2() {
            // given
            Long accountId = 1L;
            Account account = new Account(accountId);

            given(favoriteFolderRepository.findAllByAccount(account))
                    .willReturn(List.of());

            // when
            favoriteFolderService.removeByAccount(account);

            // then
            verify(favoriteFolderRepository).findAllByAccount(account);
        }
    }
}
