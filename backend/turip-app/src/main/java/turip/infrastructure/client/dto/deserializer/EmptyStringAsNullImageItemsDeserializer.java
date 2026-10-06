package turip.infrastructure.client.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import turip.infrastructure.client.dto.KoreaTourismImageResponse.Items;

public class EmptyStringAsNullImageItemsDeserializer extends JsonDeserializer<Items> {

    @Override
    public Items deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return parser.getCodec().readValue(parser, Items.class);
    }
}
