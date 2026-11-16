package turip.controller.dto.response;

public record AdminArticleImageResponse(
        String url
) {
    public static AdminArticleImageResponse from(String url) {
        return new AdminArticleImageResponse(url);
    }
}
