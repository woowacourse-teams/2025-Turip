package turip.region.domain;

import java.util.Arrays;

public enum LegalDistrictCode {
    SEOUL("서울", "11"),
    BUSAN("부산", "26"),
    INCHEON("인천", "28"),
    DAEJEON("대전", "30"),
    JEJU("제주", "50");

    private final String cityName;
    private final String code;

    LegalDistrictCode(String cityName, String code) {
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
