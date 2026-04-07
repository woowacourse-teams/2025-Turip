package turip.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
import org.springframework.context.ApplicationEventPublisher;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.domain.Role;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderExitResponse;
import turip.favorite.controller.dto.response.FavoriteFolderJoinResponse;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersDetailResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FolderInvitationTokenResponse;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.domain.event.ActionType;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.favorite.repository.dto.FavoriteFolderItemCountResult;
import turip.favorite.token.InvitationTokenProvider;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderFixture;
import turip.util.fixture.MemberFixture;

@ExtendWith(MockitoExtension.class)
class FavoriteFolderServiceTest {

    @InjectMocks
    private FavoriteFolderService favoriteFolderService;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private FavoriteFolderAccountService favoriteFolderAccountService;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private InvitationTokenProvider invitationTokenProvider;

    @DisplayName("기본 장소 찜 폴더 생성 테스트")
    @Nested
    class CreateDefaultFavoriteFolder {

        @DisplayName("기본 찜 폴더를 생성할 수 있다")
        @Test
        void createDefaultFavoriteFolder() {
            // given
            Account account = AccountFixture.createUser();

            // when
            favoriteFolderService.createDefaultFavoriteFolder(account);

            // then
            verify(favoriteFolderRepository).save(FavoriteFolder.defaultFolderOf());
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
            Long folderId = 1L;

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Account account = AccountFixture.createUser();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, folderName);

            given(favoriteFolderRepository.findAllByAccount(account))
                    .willReturn(List.of());
            given(favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder(folderName)))
                    .willReturn(favoriteFolder);

            // when
            FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, account);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(folderId),
                    () -> assertThat(response.name()).isEqualTo(folderName),
                    () -> assertThat(response.accountId()).isEqualTo(accountId),
                    () -> assertThat(response.isDefault()).isFalse()
            );
        }

        @DisplayName("해당 회원이 이미 같은 이름의 폴더를 소유하고 있는 경우 ConflictException을 발생시킨다")
        @Test
        void createCustomFavoriteFolder2() {
            // given
            String folderName = "괜찮은 소품샵 모음";

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Account account = AccountFixture.createUser();
            FavoriteFolder customFolder = FavoriteFolderFixture.createCustomFolderWithId(1L, folderName);

            given(favoriteFolderRepository.findAllByAccount(account))
                    .willReturn(List.of(customFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.createCustomFavoriteFolder(request, account))
                    .isInstanceOf(ConflictException.class);
        }

        @DisplayName("폴더 이름이 형식에 맞지 않는 경우 IllegalArgumentException이 발생한다")
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "21글자폴더입니다용21글자폴더입니다용~"})
        void createCustomFavoriteFolder3(String folderName) {
            // given
            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Account member = AccountFixture.createUser();

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
            Account savedAccount = AccountFixture.createUser();

            FavoriteFolder defaultFolder = FavoriteFolderFixture.createDefaultFolderWithId(1L);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(2L, "커스텀 폴더 1");
            given(favoriteFolderRepository.findAllByAccountOrderByFavoriteFolderAccountIdAsc(savedAccount))
                    .willReturn(List.of(defaultFolder, favoriteFolder));

            // 일괄 조회 방식으로 변경
            List<FavoriteFolderItemCountResult> placeCounts = List.of(
                    new FavoriteFolderItemCountResult(1L, 3L),
                    new FavoriteFolderItemCountResult(2L, 4L)
            );
            List<FavoriteFolderItemCountResult> memberCounts = List.of(
                    new FavoriteFolderItemCountResult(1L, 1L),
                    new FavoriteFolderItemCountResult(2L, 1L)
            );
            given(favoritePlaceRepository.countByFavoriteFolderIdsIn(List.of(1L, 2L)))
                    .willReturn(placeCounts);
            given(favoriteFolderAccountService.countByFavoriteFolderIdsIn(List.of(1L, 2L)))
                    .willReturn(memberCounts);

            // when
            FavoriteFoldersDetailResponse response = favoriteFolderService.findAllByAccount(savedAccount);

            // then
            assertAll(
                    () -> assertThat(response.favoriteFolders().get(0).placeCount()).isEqualTo(3),
                    () -> assertThat(response.favoriteFolders().get(0).memberCount()).isEqualTo(1),
                    () -> assertThat(response.favoriteFolders().get(0).name()).isEqualTo("기본 폴더"),
                    () -> assertThat(response.favoriteFolders().get(1).placeCount()).isEqualTo(4),
                    () -> assertThat(response.favoriteFolders().get(1).memberCount()).isEqualTo(1),
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
            Account savedAccount = AccountFixture.createUser();

            FavoriteFolder defaultFolder = FavoriteFolderFixture.createDefaultFolderWithId(1L);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(2L, "커스텀 폴더 1");
            given(favoriteFolderRepository.findAllByAccountOrderByFavoriteFolderAccountIdAsc(savedAccount))
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
            Account savedAccount = AccountFixture.createUser();

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

        @DisplayName("찜 폴더의 이름을 변경하고 실시간 알림 이벤트를 발행한다")
        @Test
        void updateName1() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "변경된 폴더 이름";

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, oldName);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when
            FavoriteFolderResponse response = favoriteFolderService.updateName(account, folderId, request);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(folderId),
                    () -> assertThat(response.name()).isEqualTo(newName),
                    () -> assertThat(response.accountId()).isEqualTo(accountId),
                    () -> assertThat(response.isDefault()).isFalse(),
                    () -> verify(eventPublisher).publishEvent(
                            FavoriteFolderUpdateEvent.of(folderId, ActionType.FOLDER_NAME_CHANGED))
            );
        }

        @DisplayName("favoriteFolderId에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void updateName3() {
            // given
            Long accountId = 1L;
            Long nonExistentFolderId = 999L;
            String newName = "변경된 폴더 이름";

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);
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
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "변경된 폴더 이름";

            Account requestAccount = AccountFixture.createCustomAccount(requestMemberId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, oldName);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            willThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .given(favoriteFolderAccountService).validateMembership(requestAccount, favoriteFolder);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(requestAccount, folderId, request))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("해당 회원이 이미 같은 이름의 폴더를 소유하고 있는 경우 ConflictException을 발생시킨다")
        @Test
        void updateName5() {
            // given
            Long accountId = 1L;
            Long oldFolderId = 1L;
            Long newFolderId = 2L;
            String newName = "중복된 폴더 이름";

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder oldFavoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(oldFolderId, newName);
            FavoriteFolder newFavoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(newFolderId, newName);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(newFolderId))
                    .willReturn(Optional.of(newFavoriteFolder));
            given(favoriteFolderRepository.findAllByAccount(account))
                    .willReturn(List.of(oldFavoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(account, newFolderId, request))
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

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, oldName);
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
            String newName = "새로운 폴더 이름";

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolderWithId(folderId);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(member, folderId, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }
    }

    @DisplayName("공유 찜폴더 참여자 목록 조회 테스트")
    @Nested
    class FindMembersById {

        @DisplayName("공유 찜폴더 참여자 목록을 조회할 수 있다")
        @Test
        void findMembersById1() {
            // given
            Long turipId = 1L;
            Account account = AccountFixture.createUser();

            Account memberAccount1 = new Account(2L, Role.USER, "계정1");
            Account memberAccount2 = new Account(3L, Role.USER, "계정2");
            Member member1 = new Member(1L, memberAccount1, "test1@example.com", false);
            Member member2 = new Member(2L, memberAccount2, "test2@example.com", false);

            given(favoriteFolderRepository.existsById(turipId))
                    .willReturn(true);
            given(favoriteFolderAccountService.findMembersByFavoriteFolder(turipId))
                    .willReturn(List.of(member1, member2));

            // when
            var response = favoriteFolderService.findMembersById(turipId, account);

            // then
            assertAll(
                    () -> assertThat(response.members()).hasSize(2),
                    () -> assertThat(response.members().get(0).nickname()).isEqualTo("계정1"),
                    () -> assertThat(response.members().get(1).nickname()).isEqualTo("계정2")
            );
        }

        @DisplayName("찜폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void findMembersById2() {
            // given
            Long nonExistentTuripId = 999L;
            Account account = AccountFixture.createUser();

            given(favoriteFolderRepository.existsById(nonExistentTuripId))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.findMembersById(nonExistentTuripId, account))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("요청한 account가 공유 찜폴더 참여 account가 아닌 경우 ForbiddenException을 발생시킨다")
        @Test
        void findMembersById3() {
            // given
            Long turipId = 1L;
            Account nonMemberAccount = AccountFixture.createUser();

            given(favoriteFolderRepository.existsById(turipId))
                    .willReturn(true);
            willThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .given(favoriteFolderAccountService).validateMembership(nonMemberAccount, turipId);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.findMembersById(turipId, nonMemberAccount))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(ErrorTag.FORBIDDEN.getMessage());
        }
    }

    @DisplayName("공유 찜폴더 참여 테스트")
    @Nested
    class JoinFavoriteFolder {

        @DisplayName("공유 찜폴더에 참여하고 멤버 참여 이벤트를 발행한다")
        @Test
        void joinFavoriteFolder1() {
            // given
            Long turipId = 1L;
            Long accountId = 1L;
            Long favoriteFolderAccountId = 1L;
            Member member = MemberFixture.createMember();
            Account account = member.getAccount();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(turipId, "함께 튜립");
            favoriteFolder.convertToSharedFolder();
            FavoriteFolderAccount favoriteFolderAccount = new FavoriteFolderAccount(
                    favoriteFolderAccountId, favoriteFolder, account, AccountRole.MEMBER
            );

            given(favoriteFolderRepository.findById(turipId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoriteFolderAccountService.isFolderMember(account, favoriteFolder)).willReturn(false);
            given(favoriteFolderAccountService.findOrCreate(favoriteFolder, account))
                    .willReturn(favoriteFolderAccount);

            // when
            FavoriteFolderJoinResponse response = favoriteFolderService.joinMember(turipId, member);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(favoriteFolderAccountId),
                    () -> assertThat(response.favoriteFolderId()).isEqualTo(turipId),
                    () -> assertThat(response.isShared()).isTrue(),
                    () -> assertThat(response.accountId()).isEqualTo(accountId),
                    () -> verify(eventPublisher).publishEvent(
                            FavoriteFolderUpdateEvent.of(turipId, ActionType.MEMBER_JOINED))
            );
        }

        @DisplayName("이미 참여한 공유 찜폴더에 참여요청시 409 Conflict를 발생시킨다")
        @Test
        void joinFavoriteFolder_alreadyJoined() {
            // given
            Long turipId = 1L;
            Member member = MemberFixture.createMember();
            Account account = member.getAccount();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(turipId, "함께 튜립");
            favoriteFolder.convertToSharedFolder();

            given(favoriteFolderRepository.findById(turipId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoriteFolderAccountService.isFolderMember(account, favoriteFolder)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.joinMember(turipId, member))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_ALREADY_JOINED.getMessage());
        }

        @DisplayName("찜폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void joinFavoriteFolder2() {
            // given
            Long nonExistentTuripId = 999L;
            Member member = MemberFixture.createMember();

            given(favoriteFolderRepository.findById(nonExistentTuripId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.joinMember(nonExistentTuripId, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("개인 찜폴더에 참여하려는 경우 BadRequestException을 발생시킨다")
        @Test
        void joinFavoriteFolder3() {
            // given
            Long turipId = 1L;
            Member member = MemberFixture.createMember();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(turipId, "함께 튜립");

            given(favoriteFolderRepository.findById(turipId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.joinMember(turipId, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.PERSONAL_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }

        @DisplayName("기본 찜폴더에 참여하려는 경우 BadRequestException을 발생시킨다")
        @Test
        void joinFavoriteFolder4() {
            // given
            Long turipId = 1L;
            Member member = MemberFixture.createMember();
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolderWithId(turipId);
            favoriteFolder.convertToSharedFolder();

            given(favoriteFolderRepository.findById(turipId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.joinMember(turipId, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }
    }

    @DisplayName("장소 찜 폴더 삭제 테스트")
    @Nested
    class Remove {

        @DisplayName("장소 찜 폴더를 삭제하고 폴더 삭제 이벤트를 발행한다")
        @Test
        void remove1() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            String folderName = "삭제할 폴더";

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, folderName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when
            favoriteFolderService.remove(member, folderId);

            // then
            assertAll(
                    () -> verify(favoritePlaceRepository).deleteAllByFavoriteFolder(favoriteFolder),
                    () -> verify(favoriteFolderRepository).deleteById(folderId),
                    () -> verify(eventPublisher).publishEvent(
                            FavoriteFolderUpdateEvent.of(folderId, ActionType.FOLDER_DELETED))
            );
        }

        @DisplayName("favoriteFolderId에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove3() {
            // given
            Long accountId = 1L;
            Long nonExistentFolderId = 999L;

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);

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
            Long folderId = 1L;
            String folderName = "다른 사람의 폴더";

            Account requestAccount = AccountFixture.createCustomAccount(requestAccountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, folderName);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            willThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .given(favoriteFolderAccountService).validateOwnership(requestAccount, favoriteFolder);

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

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolderWithId(folderId);

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.remove(member, folderId))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }

        @DisplayName("공유 찜 폴더를 삭제하려는 경우 BadRequestException을 발생시킨다")
        @Test
        void remove6() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account member = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createSharedFolder("공유 폴더");

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.remove(member, folderId))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.SHARED_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }
    }

    @DisplayName("공유 찜폴더 나가기 테스트")
    @Nested
    class ExitFolder {

        @DisplayName("마지막 참여자가 아닌 경우, 참여 정보만 삭제하고 멤버 탈퇴 이벤트를 발행한다")
        @Test
        void exitFolder1() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createSharedFolderWithId(folderId, "공유 폴더");

            given(favoriteFolderRepository.findByIdWithLock(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoriteFolderAccountService.countByFavoriteFolder(favoriteFolder))
                    .willReturn(2);

            // when
            FavoriteFolderExitResponse response = favoriteFolderService.exitFolder(account, folderId);

            // then
            assertAll(
                    () -> assertThat(response.isDeleted()).isFalse(),
                    () -> verify(favoriteFolderAccountService).deleteByFavoriteFolderAndAccount(favoriteFolder,
                            account),
                    () -> verify(eventPublisher).publishEvent(
                            FavoriteFolderUpdateEvent.of(folderId, ActionType.MEMBER_EXITED))
            );
        }

        @DisplayName("마지막 참여자인 경우, 폴더를 삭제하고 폴더 삭제 이벤트를 발행한다")
        @Test
        void exitFolder2() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createSharedFolderWithId(folderId, "공유 폴더");

            given(favoriteFolderRepository.findByIdWithLock(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoriteFolderAccountService.countByFavoriteFolder(favoriteFolder))
                    .willReturn(0);

            // when
            FavoriteFolderExitResponse response = favoriteFolderService.exitFolder(account, folderId);

            // then
            assertAll(
                    () -> assertThat(response.isDeleted()).isTrue(),
                    () -> verify(favoriteFolderAccountService).deleteByFavoriteFolderAndAccount(favoriteFolder,
                            account),
                    () -> verify(favoritePlaceRepository).deleteAllByFavoriteFolder(favoriteFolder),
                    () -> verify(favoriteFolderRepository).deleteById(folderId),
                    () -> verify(eventPublisher).publishEvent(
                            FavoriteFolderUpdateEvent.of(folderId, ActionType.FOLDER_DELETED))
            );
        }

        @DisplayName("찜폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void exitFolder3() {
            // given
            Long accountId = 1L;
            Long nonExistentFolderId = 999L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);

            given(favoriteFolderRepository.findByIdWithLock(nonExistentFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.exitFolder(account, nonExistentFolderId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("개인 찜폴더에서 나가려는 경우 BadRequestException을 발생시킨다")
        @Test
        void exitFolder4() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, "개인 폴더");

            given(favoriteFolderRepository.findByIdWithLock(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.exitFolder(account, folderId))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.PERSONAL_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }

        @DisplayName("기본 찜폴더에서 나가려는 경우 BadRequestException을 발생시킨다")
        @Test
        void exitFolder5() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createDefaultFolderWithId(folderId);
            favoriteFolder.convertToSharedFolder();

            given(favoriteFolderRepository.findByIdWithLock(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.exitFolder(account, folderId))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED.getMessage());
        }

        @DisplayName("해당 폴더의 참여자가 아닌 경우 ForbiddenException을 발생시킨다")
        @Test
        void exitFolder6() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account nonMemberAccount = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createSharedFolderWithId(folderId, "공유 폴더");

            given(favoriteFolderRepository.findByIdWithLock(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            willThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .given(favoriteFolderAccountService)
                    .validateMembership(nonMemberAccount, favoriteFolder);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.exitFolder(nonMemberAccount, folderId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(ErrorTag.FORBIDDEN.getMessage());
        }
    }

    @DisplayName("개인 폴더 삭제 테스트")
    @Nested
    class DeletePersonalFoldersByAccount {

        @DisplayName("개인 폴더들을 삭제할 수 있다")
        @Test
        void deletePersonalFoldersByAccount() {
            // given
            Long accountId = 1L;
            Long folderId1 = 1L;
            Long folderId2 = 2L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder personalFolder1 = FavoriteFolderFixture.createCustomFolderWithId(folderId1, "개인 폴더 1");
            FavoriteFolder personalFolder2 = FavoriteFolderFixture.createCustomFolderWithId(folderId2, "개인 폴더 2");

            given(favoriteFolderRepository.findPersonalFoldersByAccount(account))
                    .willReturn(List.of(personalFolder1, personalFolder2));

            // when
            favoriteFolderService.deletePersonalFoldersByAccount(account);

            // then
            assertAll(
                    () -> verify(favoritePlaceRepository).deleteAllByFavoriteFolder(personalFolder1),
                    () -> verify(favoritePlaceRepository).deleteAllByFavoriteFolder(personalFolder2),
                    () -> verify(favoriteFolderAccountService).deleteByFavoriteFolderAndAccount(personalFolder1,
                            account),
                    () -> verify(favoriteFolderAccountService).deleteByFavoriteFolderAndAccount(personalFolder2,
                            account),
                    () -> verify(favoriteFolderRepository).delete(personalFolder1),
                    () -> verify(favoriteFolderRepository).delete(personalFolder2)
            );
        }
    }

    @DisplayName("기본 폴더 삭제 테스트")
    @Nested
    class DeleteDefaultFolderByAccount {

        @DisplayName("계정 삭제 혹은 마이그레이션을 위해 기본 폴더를 삭제할 수 있다")
        @Test
        void deleteDefaultFolderByAccount1() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            FavoriteFolder defaultFolder = FavoriteFolderFixture.createDefaultFolderWithId(folderId);

            given(favoriteFolderRepository.findDefaultFoldersByAccount(account))
                    .willReturn(Optional.of(defaultFolder));

            // when
            favoriteFolderService.deleteDefaultFolderByAccount(account);

            // then
            assertAll(
                    () -> verify(favoritePlaceRepository).deleteAllByFavoriteFolder(defaultFolder),
                    () -> verify(favoriteFolderAccountService).deleteByFavoriteFolderAndAccount(defaultFolder,
                            account),
                    () -> verify(favoriteFolderRepository).delete(defaultFolder)
            );
        }

        @DisplayName("기본 폴더를 찾을 수 없는 경우 예외가 발생한다")
        @Test
        void deleteDefaultFolderByAccount2() {
            // given
            Long accountId = 1L;

            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);

            given(favoriteFolderRepository.findDefaultFoldersByAccount(account))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.deleteDefaultFolderByAccount(account))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.DEFAULT_FOLDER_NOT_FOUND.getMessage());
        }
    }

    @DisplayName("공유 폴더 초대 토큰 생성 테스트")
    @Nested
    class CreateInvitationCode {

        @DisplayName("초대 토큰를 생성할 수 있다")
        @Test
        void createInvitationCode() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            Member member = new Member(account, "test@naver.com", true);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, "폴더");

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(invitationTokenProvider.generateToken(member.getAccount().getId(), folderId))
                    .willReturn("test_token");

            // when
            FolderInvitationTokenResponse actual = favoriteFolderService.createInvitationToken(member, folderId);

            // then
            assertThat(actual.invitationCode()).isEqualTo("test_token");
        }

        @DisplayName("초대 토큰 최초 생성 시 공유 폴더로 변경된다")
        @Test
        void createInvitationCode_convertsToShared() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            Member member = new Member(account, "test@naver.com", true);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, "폴더");
            boolean before = favoriteFolder.isShared();

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when
            favoriteFolderService.createInvitationToken(member, folderId);
            boolean after = favoriteFolder.isShared();

            // then
            assertThat(before).isFalse();
            assertThat(after).isTrue();
        }

        @Test
        @DisplayName("공유 폴더에 참여하지 않은 멤버가 초대 토큰 생성 시 예외가 발생한다")
        void createInvitationCode_Forbidden() {
            // given
            Long accountId = 1L;
            Long folderId = 1L;
            Account account = AccountFixture.createCustomAccount(accountId, Role.USER);
            Member member = new Member(account, "test@naver.com", true);
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, "폴더");

            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            willThrow(new ForbiddenException(ErrorTag.FORBIDDEN))
                    .given(favoriteFolderAccountService).validateMembership(member.getAccount(), favoriteFolder);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.createInvitationToken(member, folderId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(ErrorTag.FORBIDDEN.getMessage());

        }
    }

    @DisplayName("공유 폴더 초대 토큰 검증 테스트")
    @Nested
    class VerifyInvitation {

        @DisplayName("유효한 토큰을 검증하고 폴더 id를 응답한다")
        @Test
        void verifyInvitation_success() {
            // given
            String token = "valid_token";
            Long folderId = 1L;
            FavoriteFolder favoriteFolder = FavoriteFolderFixture.createCustomFolderWithId(folderId, "폴더");
            Account user = AccountFixture.createUser();

            given(invitationTokenProvider.getClaimOfName(token, "fid", Long.class))
                    .willReturn(folderId);
            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(favoriteFolderAccountService.isFolderMember(user, favoriteFolder))
                    .willReturn(true);

            // when
            var response = favoriteFolderService.getInvitationDetails(token, user);

            // then
            assertAll(
                    () -> assertThat(response.turipId()).isEqualTo(folderId),
                    () -> assertThat(response.alreadyJoined()).isTrue()
            );
        }

        @DisplayName("토큰에 해당하는 폴더가 존재하지 않으면 NotFoundException을 발생시킨다")
        @Test
        void verifyInvitation_folderNotFound() {
            // given
            String token = "valid_token";
            Long folderId = 1L;
            Account user = AccountFixture.createUser();

            given(invitationTokenProvider.getClaimOfName(token, "fid", Long.class))
                    .willReturn(folderId);
            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.getInvitationDetails(token, user))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.FAVORITE_FOLDER_NOT_FOUND.getMessage());
        }

        @DisplayName("토큰이 만료되었으면 BadRequestException을 발생시킨다")
        @Test
        void verifyInvitation_expiredToken() {
            // given
            String token = "expired_token";
            Account user = AccountFixture.createUser();

            given(invitationTokenProvider.getClaimOfName(token, "fid", Long.class))
                    .willThrow(new BadRequestException(ErrorTag.INVITATION_TOKEN_EXPIRED));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.getInvitationDetails(token, user))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.INVITATION_TOKEN_EXPIRED.getMessage());
        }

        @DisplayName("토큰이 유효하지 않으면 BadRequestException을 발생시킨다")
        @Test
        void verifyInvitation_invalidToken() {
            // given
            String token = "invalid_token";
            Account user = AccountFixture.createUser();

            given(invitationTokenProvider.getClaimOfName(token, "fid", Long.class))
                    .willThrow(new BadRequestException(ErrorTag.INVALID_INVITATION_TOKEN));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.getInvitationDetails(token, user))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.INVALID_INVITATION_TOKEN.getMessage());
        }
    }
}
