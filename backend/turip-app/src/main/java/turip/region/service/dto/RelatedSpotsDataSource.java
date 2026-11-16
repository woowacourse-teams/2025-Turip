package turip.region.service.dto;

/**
 * 연관 관광지 응답 데이터의 출처.
 * <p>
 * 폴백 응답도 외부 API 정상 응답과 동일한 본문 스키마로 200을 반환하기 때문에,
 * 부하테스트에서는 응답만 보고 둘을 구분할 수 없다. 폴백은 외부 API 응답을 기다리지 않아
 * 응답 시간이 짧으므로, 두 경우를 섞어서 집계하면 고부하 구간의 latency가 실제보다
 * 좋게 보이는 왜곡이 생긴다. 이 값을 응답 헤더로 노출해 측정 단계에서 분리 집계한다.
 */
public enum RelatedSpotsDataSource {

    /**
     * 외부 API 호출이 성공했고, 그 데이터를 그대로 응답에 사용한 경우
     */
    API("api"),

    /**
     * 외부 API 호출이 모두 실패해 폴백 데이터를 사용한 경우.
     * 외부 API 장애나 호출량 제한에 의한 차단이 여기에 해당한다.
     */
    FALLBACK_FAILED("fallback-failed"),

    /**
     * 외부 API 호출 자체는 성공했지만 관광지 데이터가 비어 있어 폴백 데이터를 사용한 경우
     */
    FALLBACK_EMPTY("fallback-empty"),

    /**
     * 지역 카테고리에 매핑되는 지역 코드가 없어 외부 API를 호출하지 않은 경우
     */
    NOT_REQUESTED("not-requested");

    private final String value;

    RelatedSpotsDataSource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isFallback() {
        return this == FALLBACK_FAILED || this == FALLBACK_EMPTY;
    }
}
