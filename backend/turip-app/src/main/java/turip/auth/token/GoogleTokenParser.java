package turip.auth.token;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import turip.account.domain.Provider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;

@Slf4j
@Component
public class GoogleTokenParser implements IdTokenParser {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenParser(
            @Value("${google.client-id}") String clientId,
            @Value("${google.ios-client-id}") String iosClientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Arrays.asList(clientId, iosClientId))
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

            if (idToken == null) {
                throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID);
            }

            return idToken.getPayload();
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID, e);
        }
    }
}
