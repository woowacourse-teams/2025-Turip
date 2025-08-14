package turip.favoritefolder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

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
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
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

    @Nested
    class CreateDefaultFavoriteFolder {

        @DisplayName("커스텀 폴더를 생성할 수 있다")
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
}
