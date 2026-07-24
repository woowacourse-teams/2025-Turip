package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.domain.Member;
import turip.account.domain.nickname.RandomNicknameCreator;
import turip.account.repository.MemberRepository;
import turip.auth.service.RefreshTokenService;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.service.FavoriteFolderAccountService;
import turip.favorite.service.FavoriteFolderService;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final GuestService guestService;
    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;
    private final FavoriteFolderAccountRepository favoriteFolderAccountRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final RandomNicknameCreator randomNicknameCreator;
    private final FavoriteFolderService favoriteFolderService;
    private final FavoriteFolderAccountService favoriteFolderAccountService;

    @Transactional
    public Member create(String email) {
        try {
            Account account = accountService.create(randomNicknameCreator);
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
        member.decideMigration();
    }

    @Transactional
    public void decideMigration(Member member) {
        member.decideMigration();
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
        favoriteFolderService.deleteDefaultFolderByAccount(member.getAccount());
        favoriteFolderAccountService.updateAccount(guest.getAccount(), member.getAccount());
    }
}
