package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.springframework.dao.DuplicateKeyException;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.domain.Member;
import turip.account.domain.Role;
import turip.account.repository.MemberRepository;
import turip.auth.service.RefreshTokenService;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.InternalServerException;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.service.FavoriteFolderAccountService;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderFixture;
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
    private FavoriteFolderAccountService favoriteFolderAccountService;

    @Mock
    private GuestService guestService;

    @Mock
    private AccountService accountService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private MemberCreateService memberCreateService;

    @DisplayName("Member 생성 테스트")
    @Nested
    class CreateTest {

        @DisplayName("이메일 형식이 올바르지 않은 경우 예외를 발생시킨다.")
        @Test
        void create1() {
            // given
            String invalidEmail = "invalid-email";
            Account account = AccountFixture.createUser();
            given(accountService.create())
                    .willReturn(account);
            given(memberCreateService.save(account, invalidEmail))
                    .willThrow(new IllegalArgumentException(ErrorTag.EMAIL_INVALID));

            // when & then
            assertThatThrownBy(() -> memberService.create(invalidEmail))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.EMAIL_INVALID.getMessage());
        }

        @DisplayName("닉네임이 5번 연속 중복으로 나온 경우 InternalServerError를 발생시킨다.")
        @Test
        void create2() {
            // given
            String invalidEmail = "invalid-email";
            Account account = AccountFixture.createUser();
            given(accountService.create())
                    .willReturn(account);
            given(memberCreateService.save(account, invalidEmail))
                    .willThrow(new DuplicateKeyException(""));

            // when & then
            assertThatThrownBy(() -> memberService.create(invalidEmail))
                    .isInstanceOf(InternalServerException.class)
                    .hasMessage(ErrorTag.NICKNAME_CREATION_ERROR.getMessage());
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
            given(favoriteFolderAccountService.findAllByAccount(guestAccount))
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
            Account memberAccount = AccountFixture.createCustomAccount(1L, Role.USER);
            Account guestAccount = AccountFixture.createCustomAccount(2L, Role.USER);
            Member member = MemberFixture.createCustomMember(memberAccount, "email@test.com", true);
            Guest guest = GuestFixture.createCustomGuest(guestAccount, "device-fid-123");

            FavoriteFolder guestFolder1 = FavoriteFolderFixture.createDefaultFolderWithId(1L);
            FavoriteFolder guestFolder2 = FavoriteFolderFixture.createCustomFolderWithId(2L, "게스트 커스텀 폴더였던 것");

            FavoriteFolderAccount folderAccount1 = new FavoriteFolderAccount(guestFolder1, guestAccount,
                    AccountRole.OWNER);
            FavoriteFolderAccount folderAccount2 = new FavoriteFolderAccount(guestFolder2, guestAccount,
                    AccountRole.OWNER);

            given(favoriteContentRepository.findAllByAccount(any()))
                    .willReturn(List.of());
            given(favoriteFolderAccountService.findAllByAccount(guestAccount))
                    .willReturn(List.of(folderAccount1, folderAccount2));

            // when
            memberService.migrate(member, guest);

            // then
            assertAll(
                    () -> assertThat(folderAccount1.getAccount()).isEqualTo(memberAccount),
                    () -> assertThat(folderAccount2.getAccount()).isEqualTo(memberAccount)
            );
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
            given(favoriteFolderAccountService.findAllByAccount(any()))
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
