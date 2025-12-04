package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.auth.service.RefreshTokenService;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private GuestService guestService;

    @Mock
    private AccountService accountService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @DisplayName("Guest에서 Member로 데이터 마이그레이션 테스트")
    @Nested
    class MigrateTest {

        @DisplayName("Guest의 찜 콘텐츠를 Member의 Account로 이전한다")
        @Test
        void migrateFavoriteContents() {
            // given
            Account memberAccount = new Account(1L);
            Account guestAccount = new Account(2L);
            Member member = new Member(1L, memberAccount, Provider.GOOGLE, "providerId", "email@test.com");
            Guest guest = new Guest(1L, guestAccount, "device-fid-123");

            FavoriteContent guestFavoriteContent = new FavoriteContent(1L, LocalDate.now(), guestAccount, null);

            given(favoriteContentRepository.findAllByAccount(guestAccount))
                    .willReturn(List.of(guestFavoriteContent));
            given(favoriteFolderRepository.findAllByAccount(guestAccount))
                    .willReturn(List.of());

            // when
            memberService.migrate(member, guest);

            // then
            assertThat(guestFavoriteContent.getAccount())
                    .isEqualTo(memberAccount);
        }

        @DisplayName("Guest의 찜 폴더를 Member의 Account로 이전한다")
        @Test
        void migrateFavoriteFolders() {
            // given
            Account memberAccount = new Account(1L);
            Account guestAccount = new Account(2L);
            Member member = new Member(1L, memberAccount, Provider.GOOGLE, "providerId", "email@test.com");
            Guest guest = new Guest(1L, guestAccount, "device-fid-123");

            FavoriteFolder guestFolder1 = new FavoriteFolder(1L, guestAccount, "기본 폴더", true);
            FavoriteFolder guestFolder2 = new FavoriteFolder(2L, guestAccount, "게스트 커스텀 폴더였던 것", false);

            given(favoriteContentRepository.findAllByAccount(any()))
                    .willReturn(List.of());
            given(favoriteFolderRepository.findAllByAccount(guestAccount))
                    .willReturn(List.of(guestFolder1, guestFolder2));

            // when
            memberService.migrate(member, guest);

            // then
            assertAll(
                    () -> assertThat(guestFolder1.getAccount()).isEqualTo(memberAccount),
                    () -> assertThat(guestFolder2.getAccount()).isEqualTo(memberAccount)
            );
        }

        @DisplayName("마이그레이션이 완료되면 Guest를 삭제한다")
        @Test
        void deleteGuest() {
            // given
            Account memberAccount = new Account(1L);
            Account guestAccount = new Account(2L);
            Member member = new Member(1L, memberAccount, Provider.GOOGLE, "providerId", "email@test.com");
            Guest guest = new Guest(1L, guestAccount, "device-fid-123");

            given(favoriteContentRepository.findAllByAccount(any()))
                    .willReturn(List.of());
            given(favoriteFolderRepository.findAllByAccount(any()))
                    .willReturn(List.of());

            // when
            memberService.migrate(member, guest);

            // then
            verify(guestService).delete(guest);
        }
    }

    @DisplayName("Member 삭제(회원 탈퇴) 테스트")
    @Nested
    class DeleteTest {

        @DisplayName("Member를 삭제하고 연관된 RefreshToken, Account, 찜 데이터를 삭제한다")
        @Test
        void delete() {
            // given
            Account account = new Account(1L);
            Member member = new Member(1L, account, Provider.GOOGLE, "providerId", "email@test.com");

            // when
            memberService.delete(member);

            // then
            verify(refreshTokenService).deleteByMember(member);
            verify(memberRepository).delete(member);
            verify(accountService).deleteAccountAndFavorites(account);
        }
    }
}
