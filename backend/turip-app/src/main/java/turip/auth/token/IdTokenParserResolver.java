package turip.auth.token;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import turip.account.domain.Provider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;

@Component
public class IdTokenParserResolver {

    private final Map<Provider, IdTokenParser> parsers;

    public IdTokenParserResolver(List<IdTokenParser> parserList) {
        this.parsers = parserList.stream()
                .collect(Collectors.toMap(IdTokenParser::getProvider, Function.identity()));
    }

    public IdTokenParser resolve(Provider provider) {
        IdTokenParser parser = parsers.get(provider);
        if (parser == null) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
        return parser;
    }
}