package turip.favorite.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;

@Component
public class InvitationTokenProvider {

    private final SecretKey signingKey;
    private final Duration tokenExpiration;

    public InvitationTokenProvider(
            @Value("${invitation.jwt.secret-key}") String secretKey,
            @Value("${invitation.jwt.token-expiration}") Duration tokenExpiration) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.tokenExpiration = tokenExpiration;
    }

    public String generateToken(Long createdAccountId, Long favoriteFolderId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenExpiration.toMillis());

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuedAt(now)
                .expiration(expiry)
                .claims(Map.of("cid", createdAccountId, "fid", favoriteFolderId))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public <T> T getClaimOfName(String token, String claimName, Class<T> requiredType) {
        Claims claims = parseToken(token);
        return claims.get(claimName, requiredType);
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(ErrorTag.INVITATION_TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new BadRequestException(ErrorTag.INVALID_INVITATION_TOKEN);
        }
    }
}
