package turip.common.exception;

public enum ErrorTag {

    // 400 Bad Request
    BAD_REQUEST("올바르지 않은 요청입니다."),
    FAVORITE_FOLDER_NAME_BLANK("찜폴더 이름은 비워둘 수 없습니다."),
    FAVORITE_FOLDER_NAME_TOO_LONG("찜폴더 이름의 최대 길이를 초과했습니다."),
    DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED("기본 찜폴더에는 이 작업을 수행할 수 없습니다."),
    REGION_CATEGORY_INVALID("잘못된 지역 카테고리입니다."),
    DEVICE_FID_REQUIRED("요청 헤더에 device_fid가 존재하지 않습니다."),
    FAVORITE_PLACE_FOLDER_MISMATCH("장소 찜이 해당 폴더에 존재하지 않습니다."),
    SHARED_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED("공유 찜폴더에는 이 작업을 수행할 수 없습니다."),
    PERSONAL_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED("개인 찜폴더에는 이 작업을 수행할 수 없습니다."),

    EMAIL_INVALID("이메일 형식이 올바르지 않습니다."),
    LOGIN_ID_INVALID("아이디 형식이 올바르지 않습니다."),
    LOGIN_PASSWORD_INVALID("비밀번호 형식이 올바르지 않습니다."),
    INVITATION_TOKEN_EXPIRED("초대 토큰이 만료됐습니다."),
    INVALID_INVITATION_TOKEN("유효하지 않은 초대 토큰입니다."),

    // 401 Unauthorized
    UNAUTHORIZED("토큰 기반 인증에 실패했습니다."),
    ID_TOKEN_NOT_VALID("유효하지 않은 id token입니다."),
    ACCESS_TOKEN_EXPIRED("access token이 만료됐습니다."),
    ACCESS_TOKEN_SIGNATURE_INVALID("access token이 위조됐습니다."),
    REFRESH_TOKEN_EXPIRED("refresh token이 만료됐습니다."),
    REFRESH_TOKEN_NOT_FOUND("refresh token을 찾을 수 없습니다."),
    REFRESH_TOKEN_INVALID("유효하지 않은 refresh token입니다."),
    REFRESH_TOKEN_SIGNATURE_INVALID("refresh token이 위조됐습니다."),
    CREDENTIALS_INVALID("아이디 또는 비밀번호가 올바르지 않습니다."),

    // 403 Forbidden
    FORBIDDEN("접근 권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND("요청 정보를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다."),
    GUEST_NOT_FOUND("게스트를 찾을 수 없습니다."),
    ACCOUNT_NOT_FOUND("계정을 찾을 수 없습니다."),
    CONTENT_NOT_FOUND("컨텐츠를 찾을 수 없습니다."),
    FAVORITE_FOLDER_NOT_FOUND("찜폴더를 찾을 수 없습니다."),
    PLACE_NOT_FOUND("장소를 찾을 수 없습니다."),
    FAVORITE_PLACE_NOT_FOUND("찜한 장소를 찾을 수 없습니다."),
    CREATOR_NOT_FOUND("크리에이터를 찾을 수 없습니다."),
    FAVORITE_CONTENT_NOT_FOUND("찜한 컨텐츠를 찾을 수 없습니다."),
    FAVORITE_FOLDER_ACCOUNT_NOT_FOUND("찜폴더에 참여중인 계정 목록에서 해당 계정을 찾을 수 없습니다."),

    // 409 Conflict
    FAVORITE_FOLDER_NAME_CONFLICT("이미 존재하는 찜폴더 이름입니다."),
    FAVORITE_CONTENT_CONFLICT("이미 찜한 컨텐츠입니다."),
    FAVORITE_PLACE_IN_FOLDER_CONFLICT("해당 폴더에 이미 찜한 장소입니다."),
    LOGIN_ID_CONFLICT("중복 아이디가 존재합니다."),
    CONTENT_SAVE_CONFLICT("이미 등록된 컨텐츠입니다."),
    FAVORITE_FOLDER_ALREADY_JOINED("해당 공유 찜 폴더에 이미 참여한 멤버입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("서버에서 예기치 못한 에러가 발생했습니다."),
    ACCOUNT_CREATION_ERROR("계정 생성에 실패했습니다."),
    SSE_CONNECTION_ERROR("SSE 연결에 실패했습니다."),

    // ==== Admin ====

    // 400 Bad Request
    UNSUPPORTED_SEARCH_TYPE("지원하지 않는 검색 타입입니다."),
    YOUTUBE_API_REQUEST_FAILED("유튜브 API 요청에 실패했습니다."),
    INVALID_YOUTUBE_URL("유효하지 않은 유튜브 URL입니다."),
    INVALID_TIMELINE_FORMAT("타임라인 형식이 올바르지 않습니다."),

    // 404 Not Found
    YOUTUBE_VIDEO_NOT_FOUND("유튜브 영상을 찾을 수 없습니다."),
    CITY_NOT_FOUND("도시를 찾을 수 없습니다."),

    // 500 Internal Server Error
    YOUTUBE_API_SERVER_ERROR("유튜브 API 서버에 에러가 발생했습니다.");

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
