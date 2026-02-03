package turip.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_member__account"))
    private Account account;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "is_first_login")
    private boolean isFirstLogin;

    public Member(Long id, Account account, String email, String nickname, boolean isFirstLogin) {
        validateEmail(email);

        this.id = id;
        this.account = account;
        this.email = email;
        this.nickname = nickname;
        this.isFirstLogin = isFirstLogin;
    }

    public Member(Account account, String email, String nickname, boolean isFirstLogin) {
        this(null, account, email, nickname, isFirstLogin);
    }

    public void completeFirstLogin() {
        this.isFirstLogin = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException(ErrorTag.EMAIL_INVALID);
        }
    }
}
