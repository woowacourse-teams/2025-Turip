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
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.repository.GuestRepository;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @InjectMocks
    private GuestService guestService;

    @Mock
    private AccountService accountService;

    @Mock
    private GuestRepository guestRepository;

    @DisplayName("DeviceFid로 Guest 조회 또는 생성 테스트")
    @Nested
    class FindOrCreateByDeviceFid {

        @DisplayName("이미 존재하는 DeviceFid인 경우 기존 Guest를 반환한다")
        @Test
        void findOrCreateByDeviceFid1() {
            // given
            String deviceFid = "existing-device-fid";
            Account account = new Account(1L);
            Guest existingGuest = new Guest(1L, account, deviceFid);

            given(guestRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(existingGuest));

            // when
            Guest result = guestService.findOrCreateByDeviceFid(deviceFid);

            // then
            assertThat(result).isEqualTo(existingGuest);
            verify(accountService, never()).create();
            verify(guestRepository, never()).save(any());
        }

        @DisplayName("존재하지 않는 DeviceFid인 경우 새 Account와 Guest를 생성한다")
        @Test
        void findOrCreateByDeviceFid2() {
            // given
            String deviceFid = "new-device-fid";
            Account newAccount = new Account(1L);
            Guest newGuest = new Guest(1L, newAccount, deviceFid);

            given(guestRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());
            given(accountService.create())
                    .willReturn(newAccount);
            given(guestRepository.save(any(Guest.class)))
                    .willReturn(newGuest);

            // when
            Guest result = guestService.findOrCreateByDeviceFid(deviceFid);

            // then
            assertThat(result).isEqualTo(newGuest);
            verify(accountService).create();
            verify(guestRepository).save(any(Guest.class));
        }
    }

    @DisplayName("Guest 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("Guest를 삭제하고 연관된 Account와 찜 데이터를 삭제한다")
        @Test
        void delete() {
            // given
            Account account = new Account(1L);
            Guest guest = new Guest(1L, account, "device-fid");

            // when
            guestService.delete(guest);

            // then
            verify(guestRepository).delete(guest);
            verify(accountService).deleteAccountAndFavorites(account);
        }
    }
}
