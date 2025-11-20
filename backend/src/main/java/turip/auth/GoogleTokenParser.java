package turip.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
        System.out.println("=== GoogleTokenParser initialized ===");
        System.out.println("GOOGLE_CLIENT_ID: " + clientId);

        try {
            this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();
            System.out.println("GoogleIdTokenVerifier created successfully");
        } catch (Exception e) {
            System.out.println("Failed to create GoogleIdTokenVerifier:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Google token verifier", e);
        }
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
            System.out.println("=== Verifying ID Token ===");
            System.out.println("Token length: " + idTokenString.length());

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                System.out.println("ERROR: verify() returned null - signature or audience verification failed");
                throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID);
            }

            return idToken.getPayload();
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID);
        }
    }
}
