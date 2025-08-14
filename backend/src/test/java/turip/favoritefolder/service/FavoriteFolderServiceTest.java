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
}
