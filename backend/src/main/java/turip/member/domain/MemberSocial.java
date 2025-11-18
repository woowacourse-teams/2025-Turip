package turip.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_social", uniqueConstraints = {
        @UniqueConstraint(name = "uq_member__provider_provider_id", columnNames = {"provider", "provider_id"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSocial {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_member__account"))
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private Provider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "email")
    private String email;
}
