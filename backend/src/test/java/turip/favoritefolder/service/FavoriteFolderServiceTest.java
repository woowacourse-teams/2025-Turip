package turip.favoritefolder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.exception.custom.ConflictException;
import turip.exception.custom.ForbiddenException;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.controller.dto.request.FavoriteFolderNameRequest;
import turip.favoritefolder.controller.dto.request.FavoriteFolderRequest;
import turip.favoritefolder.controller.dto.response.FavoriteFolderResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.favoriteplace.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteFolderServiceTest {

    @InjectMocks
    private FavoriteFolderService favoriteFolderService;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @DisplayName("커스텀 장소 찜 폴더 생성 테스트")
    @Nested
    class CreateDefaultFavoriteFolder {

        @DisplayName("커스텀 찜 폴더를 생성할 수 있다")
        @Test
        void createDefaultFavoriteFolder1() {
            // given
            String folderName = "괜찮은 소품샵 모음";
            String deviceFid = "123";
            Long memberId = 1L;
            boolean isDefault = false;
            Long folderId = 1L;

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Member member = new Member(memberId, null);

            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(member));
            given(favoriteFolderRepository.existsByNameAndMember(folderName, member))
                    .willReturn(false);
            given(favoriteFolderRepository.save(FavoriteFolder.customFolderOf(member, folderName)))
                    .willReturn(new FavoriteFolder(folderId, member, folderName, isDefault));

            // when
            FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request,
                    deviceFid);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(folderId),
                    () -> assertThat(response.name()).isEqualTo(folderName),
                    () -> assertThat(response.memberId()).isEqualTo(memberId),
                    () -> assertThat(response.isDefault()).isFalse()
            );
        }

        @DisplayName("해당 회원이 이미 같은 이름의 폴더를 소유하고 있는 경우 ConflictException을 발생시킨다")
        @Test
        void createDefaultFavoriteFolder2() {
            // given
            String folderName = "괜찮은 소품샵 모음";
            String deviceFid = "123";
            Long memberId = 1L;

            FavoriteFolderRequest request = new FavoriteFolderRequest(folderName);
            Member member = new Member(memberId, null);

            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(member));
            given(favoriteFolderRepository.existsByNameAndMember(folderName, member))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.createCustomFavoriteFolder(request, deviceFid))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @DisplayName("특정 회원의 장소 찜 폴더 목록 조회 테스트")
    @Nested
    class FindAllByDeviceFid {

        @DisplayName("기기 id에 대한 찜 폴더 목록을 조회할 수 있다")
        @Test
        void findAllByDeviceFid1() {
            // given
            String deviceFid = "testDeviceFid";
            Member member = new Member(deviceFid);
            Member savedMember = new Member(1L, member.getDeviceFid());
            given(memberRepository.save(member))
                    .willReturn(savedMember);

            FavoriteFolder defaultFolder = new FavoriteFolder(1L, savedMember, "기본 폴더", true);
            FavoriteFolder favoriteFolder = new FavoriteFolder(2L, savedMember, "커스텀 폴더 1", true);
            given(favoriteFolderRepository.findAllByMember(savedMember))
                    .willReturn(List.of(defaultFolder, favoriteFolder));

            int defaultFolderPlaceCount = 3;
            int favoriteFolderPlaceCount = 4;
            given(favoritePlaceRepository.countByFavoriteFolder(defaultFolder))
                    .willReturn(defaultFolderPlaceCount);
            given(favoritePlaceRepository.countByFavoriteFolder(favoriteFolder))
                    .willReturn(favoriteFolderPlaceCount);

            // when
            FavoriteFoldersWithPlaceCountResponse response = favoriteFolderService.findAllByDeviceFid(deviceFid);

            // then
            assertAll(
                    () -> assertThat(response.favoriteFolders().get(0).placeCount()).isEqualTo(defaultFolderPlaceCount),
                    () -> assertThat(response.favoriteFolders().get(0).name()).isEqualTo("기본 폴더"),
                    () -> assertThat(response.favoriteFolders().get(1).placeCount()).isEqualTo(
                            favoriteFolderPlaceCount),
                    () -> assertThat(response.favoriteFolders().get(1).name()).isEqualTo("커스텀 폴더 1")
            );
        }

        @DisplayName("기기 id에 대한 회원이 존재하지 않는 경우, 회원을 생성하고 기본 폴더를 추가한다")
        @Test
        void findAllByDeviceFid2() {
            // given
            String deviceFid = "testDeviceFid";
            Member member = new Member(deviceFid);
            Member savedMember = new Member(1L, member.getDeviceFid());
            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());
            given(memberRepository.save(member))
                    .willReturn(savedMember);
            given(favoriteFolderRepository.findAllByMember(savedMember))
                    .willReturn(List.of());

            // when
            favoriteFolderService.findAllByDeviceFid(deviceFid);

            // then
            FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedMember);
            verify(favoriteFolderRepository).save(defaultFolder);
        }
    }

    @DisplayName("장소 찜 폴더 이름 변경 테스트")
    @Nested
    class UpdateName {

        @DisplayName("찜 폴더의 이름을 변경할 수 있다")
        @Test
        void updateName1() {
            // given
            String deviceFid = "testDeviceFid";
            Long memberId = 1L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "변경된 폴더 이름";
            boolean isDefault = false;

            Member member = new Member(memberId, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, member, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(member));
            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when
            FavoriteFolderResponse response = favoriteFolderService.updateName(deviceFid, folderId, request);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(folderId),
                    () -> assertThat(response.name()).isEqualTo(newName),
                    () -> assertThat(response.memberId()).isEqualTo(memberId),
                    () -> assertThat(response.isDefault()).isFalse()
            );
        }

        @DisplayName("deviceId에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void updateName2() {
            // given
            String deviceFid = "nonExistentDeviceFid";
            Long folderId = 1L;
            String newName = "변경된 폴더 이름";

            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(deviceFid, folderId, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("해당 id에 대한 회원이 존재하지 않습니다");
        }

        @DisplayName("favoriteFolderId에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void updateName3() {
            // given
            String deviceFid = "testDeviceFid";
            Long memberId = 1L;
            Long nonExistentFolderId = 999L;
            String newName = "변경된 폴더 이름";

            Member member = new Member(memberId, deviceFid);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(member));
            given(favoriteFolderRepository.findById(nonExistentFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(deviceFid, nonExistentFolderId, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("해당 id에 대한 폴더가 존재하지 않습니다");
        }

        @DisplayName("요청 회원 정보와 폴더 소유자의 정보가 일치하지 않는 경우 ForbiddenException을 발생시킨다")
        @Test
        void updateName4() {
            // given
            String requestDeviceFid = "requestDeviceFid";
            String ownerDeviceFid = "ownerDeviceFid";
            Long requestMemberId = 1L;
            Long ownerMemberId = 2L;
            Long folderId = 1L;
            String oldName = "기존 폴더 이름";
            String newName = "변경된 폴더 이름";
            boolean isDefault = false;

            Member requestMember = new Member(requestMemberId, requestDeviceFid);
            Member ownerMember = new Member(ownerMemberId, ownerDeviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(folderId, ownerMember, oldName, isDefault);
            FavoriteFolderNameRequest request = new FavoriteFolderNameRequest(newName);

            given(memberRepository.findByDeviceFid(requestDeviceFid))
                    .willReturn(Optional.of(requestMember));
            given(favoriteFolderRepository.findById(folderId))
                    .willReturn(Optional.of(favoriteFolder));

            // when & then
            assertThatThrownBy(() -> favoriteFolderService.updateName(requestDeviceFid, folderId, request))
                    .isInstanceOf(ForbiddenException.class);
        }
    }
}
