package turip.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.member.domain.Account;
import turip.member.domain.Guest;
import turip.member.domain.Member;
import turip.member.domain.Provider;
import turip.member.repository.AccountRepository;
import turip.member.repository.GuestRepository;
import turip.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final GuestRepository guestRepository;

    public boolean isFirstLogin(Provider provider, String providerId) {
        return !memberRepository.existsByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public Member findOrCreate(Provider provider, String providerId, String email) {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Account savedAccount = accountRepository.save(new Account());
                    Member member = new Member(savedAccount, provider, providerId, email);
                    return memberRepository.save(member);
                });
    }

    public Member getByAccountId(Long accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void migrate(Member member, Guest guest) {
        migrateFavoriteContents(member, guest);
        migrateFavoriteFolders(member, guest);
        deleteGuest(guest);
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

    private void deleteGuest(Guest guest) {
        guestRepository.delete(guest);
    }
}
