package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.controller.dto.response.MigrationAvailabilityResponse;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.repository.GuestRepository;
import turip.favorite.service.FavoriteContentService;
import turip.favorite.service.FavoriteFolderService;
import turip.favorite.service.FavoritePlaceService;
import turip.util.fixture.AccountFixture;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @InjectMocks
    private GuestService guestService;

    @Mock
    private AccountService accountService;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private FavoriteContentService favoriteContentService;

    @Mock
    private FavoritePlaceService favoritePlaceService;

    @Mock
    private FavoriteFolderService favoriteFolderService;

    @DisplayName("DeviceFid로 Guest 조회 또는 생성 테스트")
    @Nested
    class FindOrCreateByDeviceFid {

        @DisplayName("이미 존재하는 DeviceFid인 경우 기존 Guest를 반환한다")
        @Test
        void findOrCreateByDeviceFid1() {
            // given
            String deviceFid = "existing-device-fid";
            Account account = AccountFixture.createUser();
            Guest existingGuest = new Guest(1L, account, deviceFid);

            given(guestRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(existingGuest));

            // when
            Guest result = guestService.findOrCreateByDeviceFid(deviceFid);

            // then
            assertThat(result).isEqualTo(existingGuest);
            verify(accountService, never()).create(any());
            verify(guestRepository, never()).save(any());
        }

        @DisplayName("존재하지 않는 DeviceFid인 경우 새 Account와 Guest를 생성한다")
        @Test
        void findOrCreateByDeviceFid2() {
            // given
            String deviceFid = "new-device-fid";
            Account newAccount = AccountFixture.createUser();
            Guest newGuest = new Guest(1L, newAccount, deviceFid);

            given(guestRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());
            given(accountService.create(any()))
                    .willReturn(newAccount);
            given(guestRepository.save(any(Guest.class)))
                    .willReturn(newGuest);

            // when
            Guest result = guestService.findOrCreateByDeviceFid(deviceFid);

            // then
            assertThat(result).isEqualTo(newGuest);
            verify(accountService).create(any());
            verify(guestRepository).save(any(Guest.class));
        }
    }

    @DisplayName("Guest의 마이그레이션 가능한 데이터 존재 여부 조회 테스트")
    @Nested
    class CheckMigrationAvailability {

        @DisplayName("Guest의 Account에 콘텐츠 찜, 장소 찜, 커스텀 찜 폴더가 존재하지 않으면 마이그레이션을 하지 않는다")
        @Test
        void checkMigrationAvailability1() {
            // given
            Account account = AccountFixture.createUser();
            Guest guest = new Guest(1L, account, "device-fid");
            given(favoriteContentService.existsByAccount(account))
                    .willReturn(false);
            given(favoritePlaceService.existsByAccount(account))
                    .willReturn(false);
            given(favoriteFolderService.isCustomFolderExists(account))
                    .willReturn(false);

            // when
            MigrationAvailabilityResponse response = guestService.checkMigrationAvailability(guest);

            // then
            assertThat(response.availability()).isFalse();
        }

        @DisplayName("Guest의 Account에 콘텐츠 찜이 존재하면 마이그레이션을 한다")
        @Test
        void checkMigrationAvailability2() {
            // given
            Account account = AccountFixture.createUser();
            Guest guest = new Guest(1L, account, "device-fid");
            given(favoriteContentService.existsByAccount(account))
                    .willReturn(true);
            given(favoritePlaceService.existsByAccount(account))
                    .willReturn(false);
            given(favoriteFolderService.isCustomFolderExists(account))
                    .willReturn(false);

            // when
            MigrationAvailabilityResponse response = guestService.checkMigrationAvailability(guest);

            // then
            assertThat(response.availability()).isTrue();
        }

        @DisplayName("Guest의 Account에 장소 찜이 존재하면 마이그레이션을 한다")
        @Test
        void checkMigrationAvailability3() {
            // given
            Account account = AccountFixture.createUser();
            Guest guest = new Guest(1L, account, "device-fid");
            given(favoriteContentService.existsByAccount(account))
                    .willReturn(false);
            given(favoritePlaceService.existsByAccount(account))
                    .willReturn(true);
            given(favoriteFolderService.isCustomFolderExists(account))
                    .willReturn(false);

            // when
            MigrationAvailabilityResponse response = guestService.checkMigrationAvailability(guest);

            // then
            assertThat(response.availability()).isTrue();
        }

        @DisplayName("Guest의 Account에 커스텀 찜 폴더가 존재하면 마이그레이션을 한다")
        @Test
        void checkMigrationAvailability4() {
            // given
            Account account = AccountFixture.createUser();
            Guest guest = new Guest(1L, account, "device-fid");
            given(favoriteContentService.existsByAccount(account))
                    .willReturn(false);
            given(favoritePlaceService.existsByAccount(account))
                    .willReturn(false);
            given(favoriteFolderService.isCustomFolderExists(account))
                    .willReturn(true);

            // when
            MigrationAvailabilityResponse response = guestService.checkMigrationAvailability(guest);

            // then
            assertThat(response.availability()).isTrue();
        }
    }

    @DisplayName("Guest 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("Guest를 삭제하고 연관된 Account와 찜 데이터를 삭제한다")
        @Test
        void delete() {
            // given
            Account account = AccountFixture.createUser();
            Guest guest = new Guest(1L, account, "device-fid");

            // when
            guestService.delete(guest);

            // then
            verify(guestRepository).delete(guest);
            verify(accountService).deleteAccountAndFavorites(account);
        }
    }
}
