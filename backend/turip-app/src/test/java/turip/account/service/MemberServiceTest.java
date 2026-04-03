package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.domain.Member;
import turip.account.domain.Role;
import turip.account.repository.MemberRepository;
import turip.auth.service.RefreshTokenService;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.GuestFixture;
import turip.util.fixture.MemberFixture;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @Mock
    private FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private GuestService guestService;

    @Mock
    private AccountService accountService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @DisplayName("Member 생성 테스트")
    @Nested
    class CreateTest {

        @DisplayName("이메일 형식이 올바르지 않은 경우 예외를 발생시킨다.")
        @Test
        void create1() {
            // given
            String invalidEmail = "invalid-email";
            Account account = AccountFixture.createUser();
            given(accountService.create(any()))
                    .willReturn(account);

            // when & then
            assertThatThrownBy(() -> memberService.create(invalidEmail))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.EMAIL_INVALID.getMessage());
        }
    }

    @DisplayName("Guest에서 Member로 데이터 마이그레이션 테스트")
    @Nested
    class MigrateTest {

        @DisplayName("Guest의 찜 콘텐츠를 Member의 Account로 이전한다")
        @Test
        void migrateFavoriteContents() {
            // given
            Account memberAccount = AccountFixture.createCustomAccount(1L, Role.USER);
            Account guestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            Member member = MemberFixture.createCustomMember(memberAccount, "email@test.com", true);
            Guest guest = GuestFixture.createCustomGuest(guestAccount, "device-fid-123");

            FavoriteContent guestFavoriteContent = new FavoriteContent(1L, LocalDate.now(), guestAccount, null);

            given(favoriteContentRepository.findAllByAccount(guestAccount))
                    .willReturn(List.of(guestFavoriteContent));

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
            Account memberAccount = AccountFixture.createCustomAccount(1L, Role.USER);
            Account guestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            Member member = MemberFixture.createCustomMember(memberAccount, "email@test.com", true);
            Guest guest = GuestFixture.createCustomGuest(guestAccount, "device-fid-123");

            given(favoriteContentRepository.findAllByAccount(any()))
                    .willReturn(List.of());

            // when
            memberService.migrate(member, guest);

            // then
            verify(favoriteFolderRepository).deleteDefaultFolderByAccount(memberAccount);
            verify(favoriteFolderAccountRepository).updateAccount(guestAccount, memberAccount);
        }

        @DisplayName("마이그레이션이 완료되면 Guest를 삭제한다")
        @Test
        void deleteGuest() {
            // given
            Account memberAccount = AccountFixture.createCustomAccount(1L, Role.USER);
            Account guestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            Member member = MemberFixture.createCustomMember(memberAccount, "email@test.com", true);
            Guest guest = GuestFixture.createCustomGuest(guestAccount, "device-fid-123");

            given(favoriteContentRepository.findAllByAccount(any()))
                    .willReturn(List.of());

            // when
            memberService.migrate(member, guest);

            // then
            verify(guestService).delete(guest);
        }

        @DisplayName("마이그레이션이 완료되면 회원의 isMigrationDecided가 true로 변경된다.")
        @Test
        void isMigrationDecidedIsTrue() {
            // given
            Account memberAccount = AccountFixture.createCustomAccount(1L, Role.USER);
            Account guestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            Member member = MemberFixture.createCustomMember(memberAccount, "email@test.com", true);
            Guest guest = GuestFixture.createCustomGuest(guestAccount, "device-fid-123");

            given(favoriteContentRepository.findAllByAccount(any()))
                    .willReturn(List.of());

            // when
            assertThat(member.isMigrationDecided()).isFalse();
            memberService.migrate(member, guest);

            // then
            assertThat(member.isMigrationDecided()).isTrue();
        }
    }

    @DisplayName("Member 삭제(회원 탈퇴) 테스트")
    @Nested
    class DeleteTest {

        @DisplayName("Member를 삭제하고 연관된 RefreshToken, Account, 찜 데이터를 삭제한다")
        @Test
        void delete() {
            // given
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "email@test.com", true);
            // when
            memberService.delete(member);

            // then
            verify(refreshTokenService).deleteByMember(member);
            verify(memberRepository).delete(member);
            verify(accountService).deleteAccountAndFavorites(account);
        }
    }
}
