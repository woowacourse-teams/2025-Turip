package turip.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.InternalServerException;

@ExtendWith(MockitoExtension.class)
class AdminArticleImageUploaderTest {

    @InjectMocks
    private AdminArticleImageUploader adminArticleImageUploader;

    @Mock
    private S3Client s3Client;

    @Test
    @DisplayName("이미지 파일을 업로드하면 영구 public URL을 반환한다")
    void upload1() throws IOException {
        // given
        ReflectionTestUtils.setField(adminArticleImageUploader, "bucket", "test-bucket");
        ReflectionTestUtils.setField(adminArticleImageUploader, "region", "ap-northeast-2");

        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.png", "image/png", "dummy-image-bytes".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // when
        String url = adminArticleImageUploader.upload(file);

        // then
        assertThat(url).startsWith("https://test-bucket.s3.ap-northeast-2.amazonaws.com/article/");
        assertThat(url).endsWith(".png");
    }

    @Test
    @DisplayName("이미지가 아닌 파일을 업로드하면 BadRequestException을 발생시킨다")
    void upload2() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "image", "doc.pdf", "application/pdf", "dummy-bytes".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> adminArticleImageUploader.upload(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("10MB를 초과하는 파일을 업로드하면 BadRequestException을 발생시킨다")
    void upload3() {
        // given
        byte[] tooLarge = new byte[10 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile(
                "image", "big.png", "image/png", tooLarge
        );

        // when & then
        assertThatThrownBy(() -> adminArticleImageUploader.upload(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("S3 업로드가 실패하면 InternalServerException을 발생시킨다")
    void upload4() {
        // given
        ReflectionTestUtils.setField(adminArticleImageUploader, "bucket", "test-bucket");
        ReflectionTestUtils.setField(adminArticleImageUploader, "region", "ap-northeast-2");

        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.png", "image/png", "dummy-image-bytes".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("boom").build());

        // when & then
        assertThatThrownBy(() -> adminArticleImageUploader.upload(file))
                .isInstanceOf(InternalServerException.class);
    }
}
