package turip.favorite.token;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvitationTokenProviderTest {

    private static final String TEST_SECRET_KEY = "a-test-secret-key-that-is-long-enough-to-be-secure";
    private static final Duration TEST_TOKEN_EXPIRATION = Duration.ofHours(1);

    private InvitationTokenProvider invitationTokenProvider;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        invitationTokenProvider = new InvitationTokenProvider(TEST_SECRET_KEY, TEST_TOKEN_EXPIRATION);
        signingKey = Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    @DisplayName("초대 토큰 생성 테스트")
    @Test
    void generateToken() {
        // given
        Long createdAccountId = 1L;
        Long favoriteFolderId = 2L;

        // when
        String token = invitationTokenProvider.generateToken(createdAccountId, favoriteFolderId);

        // then
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date now = new Date();
        assertThat(claims.get("cid", Long.class)).isEqualTo(createdAccountId);
        assertThat(claims.get("fid", Long.class)).isEqualTo(favoriteFolderId);
        assertThat(claims.getIssuedAt()).isCloseTo(now, 1000);
        assertThat(claims.getExpiration()).isCloseTo(new Date(now.getTime() + TEST_TOKEN_EXPIRATION.toMillis()), 1000);
    }
}
