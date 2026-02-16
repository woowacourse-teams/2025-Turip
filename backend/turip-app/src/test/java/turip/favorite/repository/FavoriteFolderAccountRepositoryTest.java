package turip.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.repository.AccountRepository;
import turip.account.repository.MemberRepository;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.MemberFixture;

@DataJpaTest
@ActiveProfiles("test")
class FavoriteFolderAccountRepositoryTest {

    @Autowired
    private FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Autowired
    private FavoriteFolderRepository favoriteFolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("특정 폴더의 멤버 목록을 조회할 수 있다")
    @Test
    void findMembersByFavoriteFolder1() {
        // given
        Account account1 = accountRepository.save(AccountFixture.createEntity());
        Account account2 = accountRepository.save(AccountFixture.createEntity());
        Account account3 = accountRepository.save(AccountFixture.createEntity());

        Member member1 = memberRepository.save(MemberFixture.createCustomMember(account1, "test1@example.com", false));
        Member member2 = memberRepository.save(MemberFixture.createCustomMember(account2, "test2@example.com", false));
        memberRepository.save(MemberFixture.createCustomMember(account3, "test3@example.com", false));

        FavoriteFolder folder = favoriteFolderRepository.save(FavoriteFolder.customFolderOf("함께 튜립"));

        favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder, account2, AccountRole.MEMBER));

        // when
        List<Member> members = favoriteFolderAccountRepository.findMembersByFavoriteFolder(folder);

        // then
        assertThat(members).hasSize(2);
        assertThat(members).extracting(Member::getEmail)
                .containsExactlyInAnyOrder(member1.getEmail(), member2.getEmail());
    }

    @DisplayName("멤버가 없는 폴더의 경우 빈 리스트를 반환한다")
    @Test
    void findMembersByFavoriteFolder2() {
        // given
        FavoriteFolder folder = favoriteFolderRepository.save(FavoriteFolder.customFolderOf("빈 폴더"));

        // when
        List<Member> members = favoriteFolderAccountRepository.findMembersByFavoriteFolder(folder);

        // then
        assertThat(members).isEmpty();
    }
}
