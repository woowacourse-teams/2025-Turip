package turip.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import turip.account.domain.Role;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Component
public class JwtProvider {

    private final SecretKey signingKey;
    private final long accessTokenExpiredMs;
    private final long refreshTokenExpireMs;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpiredMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpireMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiredMs = accessTokenExpiredMs;
        this.refreshTokenExpireMs = refreshTokenExpireMs;
    }

    public String generateAccessToken(Long accountId, Role role) {
        return generateJwtToken(accountId, role, accessTokenExpiredMs);
    }

    public String generateRefreshToken(Long accountId, Role role) {
        return generateJwtToken(accountId, role, refreshTokenExpireMs);
    }

    public <T> T getClaimOfName(String token, String claim, Class<T> requiredType) {
        Claims claims = parseToken(token);
        return claims.get(claim, requiredType);
    }

    public LocalDateTime getIssuedAt(String token) {
        Claims claims = parseToken(token);
        return claims.getIssuedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public LocalDateTime getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String hashToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private String generateJwtToken(Long accountId, Role role, long expiredMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuedAt(now)
                .expiration(expiry)
                .claims(Map.of("accountId", accountId, "role", role.name()))
                .signWith(signingKey)
                .compact();
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException(ErrorTag.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(ErrorTag.ACCESS_TOKEN_SIGNATURE_INVALID);
        }
    }
}
