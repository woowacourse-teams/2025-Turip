package turip.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("deviceId가 같은지 확인할 수 있다.")
    @Test
    void isOwner1() {
        // given
        Member may1 = new Member("메이");
        Member may2 = new Member("메이");
        Member haru = new Member("하루");

        // when & then
        assertAll(
                () -> assertThat(may1.isSameDeviceId(may2)).isTrue(),
                () -> assertThat(may1.isSameDeviceId(haru)).isFalse()
        );
    }
}
