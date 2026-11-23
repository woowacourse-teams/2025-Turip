package turip.infrastructure.client.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeminiVideoInferredPlacesTest {

    @Test
    @DisplayName("응답 JSON Schema의 장소 필드는 GeminiVideoInferredPlace의 필드와 이름, 순서가 같고 모두 필수다")
    @SuppressWarnings("unchecked")
    void jsonSchema_placeFieldsMatchRecordComponents() {
        // given
        List<String> recordFields = Arrays.stream(GeminiVideoInferredPlace.class.getRecordComponents())
                .map(RecordComponent::getName)
                .toList();

        // when
        Map<String, Object> schema = GeminiVideoInferredPlaces.jsonSchema();
        Map<String, Object> places = (Map<String, Object>) ((Map<String, Object>) schema.get("properties")).get("places");
        Map<String, Object> place = (Map<String, Object>) places.get("items");
        Map<String, Object> placeProperties = (Map<String, Object>) place.get("properties");

        // then
        assertThat(placeProperties.keySet()).containsExactlyElementsOf(recordFields);
        assertThat((List<String>) place.get("required")).containsExactlyElementsOf(recordFields);
    }
}
