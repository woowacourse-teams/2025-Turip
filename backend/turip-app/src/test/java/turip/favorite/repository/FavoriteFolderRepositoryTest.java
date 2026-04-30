package turip.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderAccountFixture;
import turip.util.fixture.FavoriteFolderFixture;

@DataJpaTest
@ActiveProfiles("test")
class FavoriteFolderRepositoryTest {

    @Autowired
    private FavoriteFolderRepository favoriteFolderRepository;

    @Autowired
    private FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("계정의 모든 폴더를 조회한다")
    void findAllByAccount() {
        // given
        Account account1 = accountRepository.save(AccountFixture.createEntity());
        Account account2 = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder folder1 = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());
        FavoriteFolder folder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더1"));
        FavoriteFolder folder3 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더2"));
        FavoriteFolder folder4 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("다른 계정 폴더"));

        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder1, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder2, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder3, account1, AccountRole.MEMBER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder4, account2, AccountRole.OWNER));

        // when & then
        assertThat(favoriteFolderRepository.findAllByAccount(account1))
                .hasSize(3);
    }

    @Test
    @DisplayName("계정의 모든 폴더를 FavoriteFolderAccount ID 순으로 정렬하여 조회한다")
    void findAllByAccountOrderByFavoriteFolderAccountIdAsc() {
        // given
        Account account = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder folder1 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더1"));
        FavoriteFolder folder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("폴더2"));
        FavoriteFolder folder3 = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());

        // FavoriteFolderAccount를 특정 순서로 저장
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder3, account, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder1, account, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder2, account, AccountRole.OWNER));

        // when
        List<FavoriteFolder> result = favoriteFolderRepository.findAllByAccountOrderByFavoriteFolderAccountIdAsc(
                account);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(folder3, folder1, folder2);
    }

    @Test
    @DisplayName("계정에 커스텀 폴더가 존재하는지 확인한다")
    void existsCustomFolderByAccount_withCustomFolder() {
        // given
        Account account = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder defaultFolder = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());
        FavoriteFolder customFolder = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("커스텀 폴더"));

        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(defaultFolder, account, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(customFolder, account, AccountRole.OWNER));

        // when & then
        assertThat(favoriteFolderRepository.existsCustomFolderByAccount(account)).isTrue();
    }

    @Test
    @DisplayName("계정에 기본 폴더만 있으면 false를 반환한다")
    void existsCustomFolderByAccount_withDefaultFolderOnly() {
        // given
        Account account = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder defaultFolder = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());

        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(defaultFolder, account, AccountRole.OWNER));

        // when & then
        assertThat(favoriteFolderRepository.existsCustomFolderByAccount(account)).isFalse();
    }

    @Test
    @DisplayName("계정에 대해 혼자 참여중인 폴더들을 조회한다")
    void findAloneFoldersByAccount() {
        // given
        Account account1 = accountRepository.save(AccountFixture.createEntity());
        Account account2 = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder folder1 = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());
        FavoriteFolder folder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createSharedFolder("공유 폴더"));
        FavoriteFolder folder3 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("개인 폴더"));

        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder1, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder2, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder2, account2, AccountRole.MEMBER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder3, account1, AccountRole.OWNER));

        // when
        List<FavoriteFolder> personalFolders = favoriteFolderRepository.findAloneFoldersByAccount(account1);

        // then
        assertThat(personalFolders).hasSize(2);
        assertThat(personalFolders).containsExactly(folder1, folder3);
    }

    @Test
    @DisplayName("계정에 대한 기본 폴더를 조회한다")
    void findDefaultFoldersByAccount() {
        // given
        Account account1 = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder folder1 = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());
        FavoriteFolder folder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createSharedFolder("공유 폴더"));
        FavoriteFolder folder3 = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("개인 폴더"));

        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder1, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder2, account1, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder3, account1, AccountRole.OWNER));

        // when
        Optional<FavoriteFolder> defaultFolder = favoriteFolderRepository.findDefaultFoldersByAccount(account1);

        // then
        assertThat(defaultFolder.get()).isEqualTo(folder1);
    }
}
