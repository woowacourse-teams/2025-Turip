package turip.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.InternalServerException;

@Component
public class AdminArticleImageUploader {

    private static final Map<String, String> CONTENT_TYPE_TO_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    private final S3Client s3Client;

    @Value("${turip.article.s3.bucket}")
    private String bucket;

    @Value("${turip.article.s3.region}")
    private String region;

    public AdminArticleImageUploader(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file) {
        validateContentType(file);
        validateSize(file);

        String key = "article/" + UUID.randomUUID() + extensionFor(file.getContentType());
        putObject(file, key);

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !CONTENT_TYPE_TO_EXTENSION.containsKey(contentType)) {
            throw new BadRequestException(ErrorTag.ARTICLE_IMAGE_INVALID_TYPE);
        }
    }

    private void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException(ErrorTag.ARTICLE_IMAGE_TOO_LARGE);
        }
    }

    private String extensionFor(String contentType) {
        return CONTENT_TYPE_TO_EXTENSION.get(contentType);
    }

    private void putObject(MultipartFile file, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (S3Exception e) {
            throw new InternalServerException(ErrorTag.ARTICLE_IMAGE_UPLOAD_FAILED, e);
        }
    }
}
