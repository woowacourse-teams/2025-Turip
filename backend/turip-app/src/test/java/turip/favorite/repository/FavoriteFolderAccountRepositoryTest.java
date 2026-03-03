package turip.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import turip.favorite.repository.dto.FavoriteFolderItemCountResult;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderFixture;
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
        List<Member> members = favoriteFolderAccountRepository.findMembersByFavoriteFolderId(folder.getId());

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
        List<Member> members = favoriteFolderAccountRepository.findMembersByFavoriteFolderId(folder.getId());

        // then
        assertThat(members).isEmpty();
    }

    @Nested
    class CountByFavoriteFolderIdsIn {

        @DisplayName("폴더들의 각 멤버 수를 반환할 수 있다")
        @Test
        void countByFavoriteFolderIdsIn() {
            // given
            FavoriteFolder folder1 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더1"));
            FavoriteFolder folder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더2"));
            FavoriteFolder folder3 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더3"));
            List<Long> folderIds = List.of(folder1.getId(), folder2.getId(), folder3.getId());

            // Account 먼저 저장
            Account account1 = accountRepository.save(AccountFixture.createEntity());
            Account account2 = accountRepository.save(AccountFixture.createEntity());
            Account account3 = accountRepository.save(AccountFixture.createEntity());
            Account account4 = accountRepository.save(AccountFixture.createEntity());
            Account account5 = accountRepository.save(AccountFixture.createEntity());
            Account account6 = accountRepository.save(AccountFixture.createEntity());

            // folder1에 멤버 1명
            favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder1, account1, AccountRole.OWNER));

            // folder2에 멤버 2명
            favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder2, account2, AccountRole.OWNER));
            favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder2, account3, AccountRole.MEMBER));

            // folder3에 멤버 3명
            favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder3, account4, AccountRole.OWNER));
            favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder3, account5, AccountRole.MEMBER));
            favoriteFolderAccountRepository.save(new FavoriteFolderAccount(folder3, account6, AccountRole.MEMBER));

            // when
            List<FavoriteFolderItemCountResult> results = favoriteFolderAccountRepository.countByFavoriteFolderIdsIn(
                    folderIds);

            // then
            assertThat(results).containsAll(List.of(
                    new FavoriteFolderItemCountResult(folder1.getId(), 1L),
                    new FavoriteFolderItemCountResult(folder2.getId(), 2L),
                    new FavoriteFolderItemCountResult(folder3.getId(), 3L)
            ));
        }
    }
}
