package turip.common.exception;

public enum ErrorTag {

    FAVORITE_FOLDER_NAME_BLANK("찜폴더 이름은 비워둘 수 없습니다."),
    FAVORITE_FOLDER_NAME_TOO_LONG("찜폴더 이름은 20자를 초과할 수 없습니다."),
    IS_DEFAULT_FAVORITE_FOLDER("기본 찜폴더에는 이 작업을 수행할 수 없습니다."),
    PLACE_CATEGORY_WRONG("잘못된 지역 카테고리입니다."),

    FORBIDDEN("접근 권한이 없습니다."),

    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CONTENT_NOT_FOUND("컨텐츠를 찾을 수 없습니다."),
    FAVORITE_FOLDER_NOT_FOUND("찜폴더를 찾을 수 없습니다."),
    PLACE_NOT_FOUND("장소를 찾을 수 없습니다."),
    FAVORITE_PLACE_NOT_FOUND("찜한 장소를 찾을 수 없습니다."),
    CREATOR_NOT_FOUND("크리에이터를 찾을 수 없습니다."),

    FAVORITE_FOLDER_NAME_CONFLICT("이미 존재하는 찜폴더 이름입니다."),
    FAVORITE_CONTENT_CONFLICT("이미 찜한 컨텐츠입니다."),
    FAVORITE_PLACE_CONFLICT("이미 찜한 장소입니다.");

    private final String message;

    ErrorTag(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
