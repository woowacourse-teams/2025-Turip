package turip.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;
import turip.member.domain.Provider;

@Component
public class GoogleTokenParser implements IdTokenParser {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenParser(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

    @Override
    public String getProviderId(String idToken) {
        Payload payload = parseIdToken(idToken);
        return payload.getSubject();
    }

    @Override
    public String getEmail(String idToken) {
        Payload payload = parseIdToken(idToken);
        return payload.getEmail();
    }

    private Payload parseIdToken(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID);
        }
    }
}
