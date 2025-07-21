package turip.content.controller.dto.response;

public record ContentCountResponse(
        int count
) {

    public static ContentCountResponse from(int count) {
        return new ContentCountResponse(count);
    }
}
