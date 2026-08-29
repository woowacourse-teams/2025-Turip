package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.controller.dto.response.AdminBookmarkStatusResponse;
import turip.creator.domain.Creator;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;
import turip.region.domain.City;
import turip.region.domain.Country;
import turip.util.fixture.AccountFixture;

@ExtendWith(MockitoExtension.class)
class AdminBookmarkServiceTest {

    @InjectMocks
    private AdminBookmarkService adminBookmarkService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @DisplayName("콘텐츠별 ADMIN 계정 북마크 상태 조회 기능 테스트")
    @Nested
    class FindAccountBookmarkStatuses {

        @DisplayName("전체 ADMIN 계정과 각 계정의 지난주 북마크 여부를 반환한다")
        @Test
        void findAccountBookmarkStatuses1() {
            // given
            Long contentId = 1L;
            Creator creator = new Creator("여행하는 메이", "profile");
            Country country = new Country("대한민국", "image");
            City city = new City(country, null, "속초", "image");
            Content content = new Content(contentId, creator, city, "제목", "url", LocalDate.now());

            Account admin1 = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Account admin2 = AccountFixture.createCustomAccount(2L, Role.ADMIN);

            given(contentRepository.findById(contentId)).willReturn(Optional.of(content));
            given(accountRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin1, admin2));
            given(favoriteContentRepository.findByAccountIdInAndContentId(List.of(1L, 2L), contentId))
                    .willReturn(List.of(new FavoriteContent(LocalDate.now(), admin1, content)));

            // when
            List<AdminBookmarkStatusResponse> response = adminBookmarkService.findAdminAccountBookmarkStatuses(
                    contentId);

            // then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).accountId()).isEqualTo(1L);
            assertThat(response.get(0).isBookmarked()).isTrue();
            assertThat(response.get(1).accountId()).isEqualTo(2L);
            assertThat(response.get(1).isBookmarked()).isFalse();
        }
    }
}
