package turip.region.domain;

import java.util.Arrays;

/**
 * 한국관광공사 TourAPI의 법정동 코드를 튜립 서버의 city name으로 매핑하여 관광사진 조회에 사용
 */
public enum TourApiLegalDongCode {
    SEOUL("서울", "11"),
    BUSAN("부산", "26"),
    INCHEON("인천", "28"),
    DAEJEON("대전", "30"),
    JEJU("제주", "50");

    private final String cityName;
    private final String code;

    TourApiLegalDongCode(String cityName, String code) {
        this.cityName = cityName;
        this.code = code;
    }

    public static String getCodeByCityName(String cityName) {
        return Arrays.stream(values())
                .filter(district -> cityName.contains(district.cityName))
                .findFirst()
                .map(district -> district.code)
                .orElse(null);
    }

    public static boolean isSupportedCity(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return false;
        }
        return Arrays.stream(values())
                .anyMatch(district -> cityName.equals(district.cityName));
    }

    public String getCode() {
        return code;
    }

    public String getCityName() {
        return cityName;
    }
}
