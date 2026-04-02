package turip.favorite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;
import turip.container.TestContainerConfig;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderAccountFixture;
import turip.util.fixture.FavoriteFolderFixture;

@DataJpaTest
@ActiveProfiles("test-mysql")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FavoriteFolderRepositoryTest extends TestContainerConfig {

    @Autowired
    private FavoriteFolderRepository favoriteFolderRepository;

    @Autowired
    private FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

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
    @DisplayName("계정에 대한 폴더를 삭제할 때, 공유 폴더는 삭제하지 않는다")
    void deletePersonalFoldersByAccount() {
        // given
        Account account = accountRepository.save(AccountFixture.createEntity());

        // Personal folders (isShared = false)
        FavoriteFolder personalFolder1 = favoriteFolderRepository.save(
                FavoriteFolderFixture.createCustomFolder("개인 폴더1"));
        FavoriteFolder personalFolder2 = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());

        // Shared folder (isShared = true)
        FavoriteFolder sharedFolder = favoriteFolderRepository.save(FavoriteFolderFixture.createCustomFolder("공유 폴더"));
        sharedFolder.convertToSharedFolder();
        favoriteFolderRepository.save(sharedFolder);

        // 폴더와 계정 연결
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(personalFolder1, account, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(personalFolder2, account, AccountRole.OWNER));
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(sharedFolder, account, AccountRole.OWNER));

        // when
        favoriteFolderRepository.deletePersonalFoldersByAccount(account);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(favoriteFolderRepository.findById(personalFolder1.getId())).isEmpty();
        assertThat(favoriteFolderRepository.findById(personalFolder2.getId())).isEmpty();
        assertThat(favoriteFolderRepository.findById(sharedFolder.getId())).isPresent();
    }

    @Test
    @DisplayName("특정 계정의 기본 폴더를 삭제할 수 있다")
    void deleteDefaultFoldersByAccount() {
        // given
        Account account = accountRepository.save(AccountFixture.createEntity());

        FavoriteFolder defaultFolder = favoriteFolderRepository.save(FavoriteFolderFixture.createDefaultFolder());

        // 폴더와 계정 연결
        favoriteFolderAccountRepository.save(
                FavoriteFolderAccountFixture.createFavoriteFolderAccount(defaultFolder, account, AccountRole.OWNER));

        // when
        favoriteFolderRepository.deleteDefaultFolderByAccount(account);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(favoriteFolderRepository.findById(defaultFolder.getId())).isEmpty();
        assertThat(favoriteFolderAccountRepository.findByFavoriteFolderAndAccount(defaultFolder, account)).isEmpty();
    }
}
