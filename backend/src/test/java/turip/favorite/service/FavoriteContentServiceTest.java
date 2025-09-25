package turip.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.content.ContentsDetailWithLoadableResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.content.service.ContentPlaceService;
import turip.creator.domain.Creator;
import turip.favorite.controller.dto.request.FavoriteContentRequest;
import turip.favorite.controller.dto.response.FavoriteContentResponse;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;
import turip.member.domain.Member;
import turip.region.domain.City;
import turip.region.domain.Country;
import turip.region.domain.Province;

@ExtendWith(MockitoExtension.class)
class FavoriteContentServiceTest {

    @InjectMocks
    private FavoriteContentService favoriteContentService;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ContentPlaceService contentPlaceService;

    @DisplayName("찜을 생성할 수 있다")
    @Test
    void createFavoriteContent() {
        // given
        Long contentId = 1L;
        String deviceFid = "testDeviceFid";

        FavoriteContentRequest request = new FavoriteContentRequest(contentId);
        Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
        Province province = new Province(1L, "강원도");
        City city = new City(1L, country, province, "속초", "시 이미지 경로");
        Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url", LocalDate.of(2025, 7, 8));
        Member member = new Member(1L, deviceFid);
        FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), member, content);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.of(content));
        given(favoriteContentRepository.existsByMemberIdAndContentId(any(), eq(contentId)))
                .willReturn(false);
        given(favoriteContentRepository.save(any(FavoriteContent.class)))
                .willReturn(favoriteContent);

        // when
        FavoriteContentResponse response = favoriteContentService.create(request, member);

        // then
        assertThat(response.content().id()).isEqualTo(contentId);
    }

    @DisplayName("컨텐츠가 존재하지 않는 경우 예외가 발생한다")
    @Test
    void createFavoriteException() {
        // given
        Long contentId = 10L;
        FavoriteContentRequest request = new FavoriteContentRequest(contentId);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> favoriteContentService.create(request, any()))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("이미 찜한 컨텐츠인 경우 예외가 발생한다")
    @Test
    void createFavoriteException2() {
        // given
        Long contentId = 5L;
        String deviceFid = "testDeviceFid";
        FavoriteContentRequest request = new FavoriteContentRequest(contentId);

        Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
        Province province = new Province(1L, "강원도");
        City city = new City(1L, country, province, "속초", "시 이미지 경로");
        Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url", LocalDate.of(2025, 7, 8));
        Member member = new Member(1L, deviceFid);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.of(content));
        given(favoriteContentRepository.existsByMemberIdAndContentId(any(), eq(contentId)))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> favoriteContentService.create(request, member))
                .isInstanceOf(ConflictException.class);
    }

    @DisplayName("내 찜 목록을 페이징 조회할 수 있다")
    @Test
    void findMyFavoriteContents1() {
        // given
        String deviceFid = "testDeviceFid";
        int pageSize = 2;
        long lastContentId = 0L;

        Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
        Province province = new Province(1L, "강원도");
        City city = new City(1L, country, province, "속초", "시 이미지 경로");
        Content content1 = new Content(1L, creator, city, "뭉치의 속초 브이로그 1편", "속초 브이로그 Url 1", LocalDate.of(2025, 7, 8));
        Content content2 = new Content(2L, creator, city, "뭉치의 속초 브이로그 2편", "속초 브이로그 Url 2", LocalDate.of(2025, 7, 8));
        List<Content> contents = List.of(content1, content2);
        Member member = new Member(1L, deviceFid);

        given(favoriteContentRepository.findMyFavoriteContentsByDeviceFid(eq(deviceFid), eq(Long.MAX_VALUE), any()))
                .willReturn(new SliceImpl<>(contents));
        given(contentPlaceService.calculateDurationDays(1L))
                .willReturn(2); // content1 1박 2일
        given(contentPlaceService.calculateDurationDays(2L))
                .willReturn(3); // content2 2박 3일
        given(contentRepository.existsById(1L))
                .willReturn(true);
        given(contentRepository.existsById(2L))
                .willReturn(true);

        ContentsDetailWithLoadableResponse response = favoriteContentService.findMyFavoriteContents(member,
                pageSize, lastContentId);

        // then
        assertAll(
                () -> assertThat(response.contents()).hasSize(2),
                () -> assertThat(response.loadable()).isFalse(),
                () -> assertThat(response.contents().getFirst().tripDuration().days()).isEqualTo(2),
                () -> assertThat(response.contents().get(1).tripDuration().days()).isEqualTo(3)
        );
    }

    @DisplayName("내 찜 목록을 페이징 조회할 때, 컨텐츠 id에 대한 컨텐츠가 존재하지 않는 경우 NotFoundException을 발생시킨다.")
    @Test
    void findMyFavoriteContents2() {
        // given
        String deviceFid = "testDeviceFid";
        int pageSize = 2;
        long lastContentId = 0L;

        Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
        Province province = new Province(1L, "강원도");
        City city = new City(1L, country, province, "속초", "시 이미지 경로");
        Content content1 = new Content(1L, creator, city, "뭉치의 속초 브이로그 1편", "속초 브이로그 Url 1", LocalDate.of(2025, 7, 8));
        Content content2 = new Content(2L, creator, city, "뭉치의 속초 브이로그 2편", "속초 브이로그 Url 2", LocalDate.of(2025, 7, 8));
        List<Content> contents = List.of(content1, content2);
        Member member = new Member(1L, deviceFid);

        given(favoriteContentRepository.findMyFavoriteContentsByDeviceFid(eq(deviceFid), eq(Long.MAX_VALUE), any()))
                .willReturn(new SliceImpl<>(contents));
        given(contentPlaceService.calculateDurationDays(1L))
                .willReturn(2); // content1 1박 2일
        given(contentPlaceService.calculateDurationDays(2L))
                .willReturn(3); // content2 2박 3일
        given(contentRepository.existsById(1L))
                .willReturn(true);
        given(contentRepository.existsById(2L))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> favoriteContentService.findMyFavoriteContents(member, pageSize, lastContentId))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("찜 삭제 테스트")
    @Nested
    class RemoveFavoriteContent {

        @DisplayName("찜을 삭제할 수 있다")
        @Test
        void deleteFavoriteContent1() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
            Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
            Province province = new Province(1L, "강원도");
            City city = new City(1L, country, province, "속초", "시 이미지 경로");
            Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url",
                    LocalDate.of(2025, 7, 8));
            Member member = new Member(1L, deviceFid);
            FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), member, content);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(favoriteContentRepository.findByMemberIdAndContentId(any(), eq(contentId)))
                    .willReturn(Optional.of(favoriteContent));

            // when
            favoriteContentService.remove(member, contentId);

            // then
            assertThat(favoriteContentRepository.findById(contentId)).isEmpty();
        }

        @DisplayName("삭제하려는 찜의 contentId에 대한 컨텐츠가 존재하지 않으면 에러가 발생한다.")
        @Test
        void deleteFavoriteContent2() {
            // given
            Long contentId = 1L;
            Member member = new Member(1L, "testDeviceFid");

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteContentService.remove(member, contentId))
                    .isInstanceOf(NotFoundException.class);
        }

        @DisplayName("삭제하려는 찜이 찜 상태가 아니라면 에러를 발생시킨다.")
        @Test
        void deleteFavoriteContent4() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
            Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
            Province province = new Province(1L, "강원도");
            City city = new City(1L, country, province, "속초", "시 이미지 경로");
            Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url",
                    LocalDate.of(2025, 7, 8));
            Member member = new Member(1L, deviceFid);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(favoriteContentRepository.findByMemberIdAndContentId(any(), eq(contentId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteContentService.remove(member, contentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(ErrorTag.FAVORITE_CONTENT_NOT_FOUND.getMessage());
        }
    }
}
