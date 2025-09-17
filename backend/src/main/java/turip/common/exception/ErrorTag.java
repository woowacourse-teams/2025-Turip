package turip.common.exception;

public enum ErrorTag {

    // 400 Bad Request
    BAD_REQUEST("올바르지 않은 요청입니다."),
    FAVORITE_FOLDER_NAME_BLANK("찜폴더 이름은 비워둘 수 없습니다."),
    FAVORITE_FOLDER_NAME_TOO_LONG("찜폴더 이름의 최대 길이를 초과했습니다."),
    DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED("기본 찜폴더에는 이 작업을 수행할 수 없습니다."),
    REGION_CATEGORY_INVALID("잘못된 지역 카테고리입니다."),

    // 403 Forbidden
    FORBIDDEN("접근 권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND("요청 정보를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CONTENT_NOT_FOUND("컨텐츠를 찾을 수 없습니다."),
    FAVORITE_FOLDER_NOT_FOUND("찜폴더를 찾을 수 없습니다."),
    PLACE_NOT_FOUND("장소를 찾을 수 없습니다."),
    FAVORITE_PLACE_NOT_FOUND("찜한 장소를 찾을 수 없습니다."),
    CREATOR_NOT_FOUND("크리에이터를 찾을 수 없습니다."),
    FAVORITE_CONTENT_NOT_FOUND("찜한 컨텐츠를 찾을 수 없습니다."),

    // 409 Conflict
    FAVORITE_FOLDER_NAME_CONFLICT("이미 존재하는 찜폴더 이름입니다."),
    FAVORITE_CONTENT_CONFLICT("이미 찜한 컨텐츠입니다."),
    FAVORITE_PLACE_IN_FOLDER_CONFLICT("해당 폴더에 이미 찜한 장소입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("서버에서 예기치 못한 에러가 발생했습니다.");

    private final String message;

    ErrorTag(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
