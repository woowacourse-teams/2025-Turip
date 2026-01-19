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
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mindrot.jbcrypt.BCrypt;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TuripMember {

    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-z0-9_-]{5,20}$");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_turip_member__member"))
    private Member member;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "login_password", nullable = false)
    private String loginPassword;

    public TuripMember(Long id, Member member, String loginId, String loginPassword) {
        validateLoginId(loginId);
        validateLoginPassword(loginPassword);

        this.id = id;
        this.member = member;
        this.loginId = loginId;
        this.loginPassword = BCrypt.hashpw(loginPassword, BCrypt.gensalt());
    }

    public TuripMember(Member member, String loginId, String loginPassword) {
        this(null, member, loginId, loginPassword);
    }

    private void validateLoginId(String loginId) {
        if (loginId == null || !LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_ID_INVALID);
        }
    }

    private void validateLoginPassword(String loginPassword) {
        if (loginPassword == null) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_PASSWORD_INVALID);
        }

        if (loginPassword.length() < MIN_PASSWORD_LENGTH || loginPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_PASSWORD_INVALID);
        }

        if (!LOWERCASE_PATTERN.matcher(loginPassword).find()) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_PASSWORD_INVALID);
        }

        if (!DIGIT_PATTERN.matcher(loginPassword).find()) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_PASSWORD_INVALID);
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(loginPassword).find()) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_PASSWORD_INVALID);
        }

        if (!loginPassword.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]+$")) {
            throw new IllegalArgumentException(ErrorTag.LOGIN_PASSWORD_INVALID);
        }
    }
}
