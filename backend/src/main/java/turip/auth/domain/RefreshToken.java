package turip.auth.domain;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.member.domain.Member;

@Entity
@Getter
@Table(name = "refresh_token", uniqueConstraints = {
        @UniqueConstraint(name = "uq_refresh_token__member_id_device_fid", columnNames = {"member_id", "device_fid"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token__member"))
    private Member member;

    @Column(name = "device_fid", nullable = false)
    private String deviceFid;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public RefreshToken(Member member, String deviceFid, String tokenHash, LocalDateTime issuedAt,
                        LocalDateTime expiredAt) {
        this.member = member;
        this.deviceFid = deviceFid;
        this.tokenHash = tokenHash;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}
