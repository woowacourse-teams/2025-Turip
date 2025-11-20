package turip.auth.token;

import turip.member.domain.Provider;

public interface IdTokenParser {

    Provider getProvider();

    String getProviderId(String idToken);

    String getEmail(String idToken);
}
