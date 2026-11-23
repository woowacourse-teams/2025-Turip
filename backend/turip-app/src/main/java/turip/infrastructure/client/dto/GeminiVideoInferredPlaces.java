package turip.infrastructure.client.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini 영상 장소 추론 응답 본문. Structured outputs로 이 형태의 JSON만 생성하도록 강제한다.
 *
 * @param places 영상에서 찾은 방문 장소 목록
 */
public record GeminiVideoInferredPlaces(
        List<GeminiVideoInferredPlace> places
) {

    public GeminiVideoInferredPlaces {
        places = places == null ? List.of() : places;
    }

    /**
     * 응답 JSON Schema. 모델은 스키마의 키 순서대로 출력하므로 순서가 유지되는 Map을 사용한다.
     * 필드를 바꿀 때는 {@link GeminiVideoInferredPlace}와 함께 수정해야 한다.
     */
    public static Map<String, Object> jsonSchema() {
        Map<String, Object> placeProperties = new LinkedHashMap<>();
        placeProperties.put("placeName", Map.of(
                "type", "string",
                "description", "장소명"));
        placeProperties.put("confidence", Map.of(
                "type", "integer",
                "minimum", 0,
                "maximum", 100,
                "description", "판단한 장소의 정확도. 텍스트 근거 없이 시각적 특징으로만 추론했다면 낮게 잡는다."));
        placeProperties.put("timeSeconds", Map.of(
                "type", List.of("integer", "null"),
                "description", "장소가 등장한 시점(초). 알 수 없으면 null"));
        placeProperties.put("evidence", Map.of(
                "type", "string",
                "description", "판단 근거 서술"));

        Map<String, Object> place = new LinkedHashMap<>();
        place.put("type", "object");
        place.put("properties", placeProperties);
        place.put("required", List.copyOf(placeProperties.keySet()));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", Map.of("places", Map.of(
                "type", "array",
                "description", "영상에 등장하는 방문 장소 목록. 장소를 하나도 찾을 수 없으면 빈 배열",
                "items", place)));
        schema.put("required", List.of("places"));
        return schema;
    }
}
