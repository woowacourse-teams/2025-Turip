package turip.infrastructure.client;

/**
 * 한국관광공사 TourAPI 공통 요청 파라미터 상수
 */
public final class KoreaTourismApiConstants {

    /**
     * MobileOS 파라미터 - 호출 OS 구분 (ETC: 기타)
     */
    public static final String MOBILE_OS = "ETC";

    /**
     * MobileApp 파라미터 - 서비스(앱)명
     */
    public static final String MOBILE_APP = "Turip";

    /**
     * _type 파라미터 - 응답 메시지 형식 (json)
     */
    public static final String RESPONSE_TYPE = "json";

    private KoreaTourismApiConstants() {
    }
}
