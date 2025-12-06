package turip.auth.token;

import turip.account.domain.Provider;

public interface IdTokenParser {

    Provider getProvider();

    String getProviderId(String idToken);

    String getEmail(String idToken);
}
