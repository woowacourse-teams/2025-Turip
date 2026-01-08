package turip.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.domain.Member;
import turip.account.repository.MemberRepository;
import turip.auth.service.RefreshTokenService;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final GuestService guestService;
    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public Member create(String email) {
        Account account = accountService.create();
        return memberRepository.save(new Member(account, email, true));
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
        favoriteFolderRepository.deleteByAccountAndIsDefault(member.getAccount(), true);

        favoriteFolderRepository.findAllByAccount(guest.getAccount())
                .forEach(favoriteFolder -> favoriteFolder.updateAccount(member.getAccount()));
    }
}
