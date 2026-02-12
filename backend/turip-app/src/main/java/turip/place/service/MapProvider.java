package turip.place.service;

public enum MapProvider {
    GOOGLE,
    KAKAO;

    public static MapProvider getProviderFromCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return GOOGLE;
        }
        for (char c : categoryName.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                return KAKAO;
            }
        }
        return GOOGLE;
    }
}
