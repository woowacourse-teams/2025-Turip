package turip.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public String generateAccessToken(Long accountId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiredMs);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuedAt(now)
                .expiration(expiry)
                .claim("accountId", accountId)
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(Long accountId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpireMs);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuedAt(now)
                .expiration(expiry)
                .claim("accountId", accountId)
                .signWith(signingKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
}
