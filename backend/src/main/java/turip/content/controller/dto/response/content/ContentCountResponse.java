package turip.content.controller.dto.response.content;

public record ContentCountResponse(
        int count
) {

    public static ContentCountResponse from(int count) {
        return new ContentCountResponse(count);
    }
}
