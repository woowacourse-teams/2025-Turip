package turip.auth.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Provider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdTokenParserResolver 테스트")
class IdTokenParserResolverTest {

    @Mock
    private GoogleTokenParser googleTokenParser;

    @Mock
    private AppleTokenParser appleTokenParser;

    private IdTokenParserResolver resolver;

    @BeforeEach
    void setUp() {
        when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
        when(appleTokenParser.getProvider()).thenReturn(Provider.APPLE);

        List<IdTokenParser> parsers = List.of(googleTokenParser, appleTokenParser);
        resolver = new IdTokenParserResolver(parsers);
    }

    @Test
    @DisplayName("GOOGLE Provider에 대해 GoogleTokenParser를 반환한다")
    void resolveGoogleTokenParser() {
        // when
        IdTokenParser result = resolver.resolve(Provider.GOOGLE);

        // then
        assertThat(result).isEqualTo(googleTokenParser);
    }

    @Test
    @DisplayName("APPLE Provider에 대해 AppleTokenParser를 반환한다")
    void resolveAppleTokenParser() {
        // when
        IdTokenParser result = resolver.resolve(Provider.APPLE);

        // then
        assertThat(result).isEqualTo(appleTokenParser);
    }
}