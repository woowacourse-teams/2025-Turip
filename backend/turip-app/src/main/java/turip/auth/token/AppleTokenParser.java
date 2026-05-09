package turip.auth.token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import turip.account.domain.Provider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;

@Slf4j
@Component
public class AppleTokenParser implements IdTokenParser {

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public AppleTokenParser() {
        try {
            this.jwtProcessor = createJwtProcessor();
        } catch (Exception e) {
            throw new IllegalStateException("AppleTokenParser 초기화 실패");
        }
    }

    private ConfigurableJWTProcessor<SecurityContext> createJwtProcessor() throws Exception {
        ConfigurableJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(APPLE_JWKS_URL));
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        processor.setJWSKeySelector(keySelector);
        return processor;
    }

    @Override
    public Provider getProvider() {
        return Provider.APPLE;
    }

    @Override
    public String getProviderId(String idToken) {
        JWTClaimsSet claimsSet = parseIdToken(idToken);
        return claimsSet.getSubject();
    }

    @Override
    public String getEmail(String idToken) {
        JWTClaimsSet claimsSet = parseIdToken(idToken);
        try {
            return claimsSet.getStringClaim("email");
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID, e);
        }
    }

    private JWTClaimsSet parseIdToken(String idToken) {
        try {
            return jwtProcessor.process(idToken, null);
        } catch (Exception e) {
            log.error("Failed to parse Apple ID token", e);
            throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID, e);
        }
    }
}
