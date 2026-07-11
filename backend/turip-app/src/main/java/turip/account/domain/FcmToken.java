package turip.account.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.common.domain.BaseTimeEntity;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;

@Entity
@Getter
@Table(name = "fcm_token", uniqueConstraints = {
        @UniqueConstraint(name = "uk_fcm_token__account_id_device_fid", columnNames = {"account_id", "device_fid"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseTimeEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fcm_token__account"))
    private Account account;

    @Column(name = "device_fid", nullable = false)
    private String deviceFid;

    @Column(name = "token", length = 500, nullable = false, unique = true)
    private String token;

    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled;

    public FcmToken(Account account, String deviceFid, String token) {
        validateToken(token);
        this.account = account;
        this.deviceFid = deviceFid;
        this.token = token;
        this.notificationEnabled = true;
    }

    public void updateToken(String token) {
        validateToken(token);
        this.token = token;
    }

    public void changeNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    private void validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new BadRequestException(ErrorTag.FCM_TOKEN_BLANK);
        }
    }
}
