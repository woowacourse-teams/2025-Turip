package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.domain.Member;
import turip.account.repository.MemberRepository;
import turip.auth.service.RefreshTokenService;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.favorite.repository.FavoriteFolderRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FavoriteFolderAccountRepository favoriteFolderAccountRepository;
    private final GuestService guestService;
    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public Member create(String email) {
        Account account = accountService.create();
        try {
            Member member = new Member(account, email, true);
            return memberRepository.save(member);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getErrorTag());
        }
    }

    public Member getByAccountId(Long accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void migrate(Member member, Guest guest) {
        migrateFavoriteContents(member, guest);
        migrateFavoriteFolders(member, guest);
        guestService.delete(guest);
    }

    @Transactional
    public void delete(Member member) {
        refreshTokenService.deleteByMember(member);
        memberRepository.delete(member);
        accountService.deleteAccountAndFavorites(member.getAccount());
    }

    private void migrateFavoriteContents(Member member, Guest guest) {
        favoriteContentRepository.findAllByAccount(guest.getAccount())
                .forEach(favoriteContent -> favoriteContent.updateAccount(member.getAccount()));
    }

    private void migrateFavoriteFolders(Member member, Guest guest) {
        // guest account에 대한 FavoriteFolderAccount의 account를 member로 바꿔주기(default 폴더는 지우기)
        favoriteFolderAccountRepository.findAllByAccount(guest.getAccount())
                .forEach(favoriteFolderAccount -> {
                    if (favoriteFolderAccount.getFavoriteFolder().isDefault()) {
                        favoriteFolderRepository.delete(favoriteFolderAccount.getFavoriteFolder());
                        favoriteFolderAccountRepository.delete(favoriteFolderAccount);
                    }
                    favoriteFolderAccount.updateAccount(member.getAccount());
                });
    }
}
