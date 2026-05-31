package turip.region.domain;

import java.util.Arrays;

public enum LegalDistrictCode {
    SEOUL("서울", "1100000000"),
    BUSAN("부산", "2600000000"),
    INCHEON("인천", "2800000000"),
    DAEJEON("대전", "3000000000"),
    JEJU("제주", "5000000000");

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

    public String getCode() {
        return code;
    }

    public String getCityName() {
        return cityName;
    }
}
