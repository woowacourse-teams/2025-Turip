package turip.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtProvider {

    private final Key signingKey;
    private final long accessTokenExpiredMs;
    private final long refreshTokenExpireMs;

    public JwtProvider(String secretKey, long accessTokenExpiredMs, long refreshTokenExpireMs) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiredMs = accessTokenExpiredMs;
        this.refreshTokenExpireMs = refreshTokenExpireMs;
    }

    public String generateAccessToken(String accountId) {
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

    public String generateRefreshToken(String accountId) {
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
}
