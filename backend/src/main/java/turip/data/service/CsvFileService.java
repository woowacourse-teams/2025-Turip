package turip.data.service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsvFileService {
    public boolean isValidCsvFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return false;
        }

        long fileSize = Files.size(filePath);
        if (fileSize < 10) {
            log.warn("CSV 파일이 너무 작습니다: {} bytes", fileSize);
            return false;
        }

        String fileName = filePath.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".csv")) {
            log.warn("CSV 파일 확장자가 아닙니다: {}", fileName);
            return false;
        }

        return true;
    }

    public Path downloadCsvFromUrl(String urlString) throws IOException {
        log.info("CSV 파일 다운로드 시작: {}", urlString);

        URL url = new URL(urlString);
        Path tempFile = Files.createTempFile("csv_import_", ".csv");

        try (var inputStream = url.openStream()) {
            long bytesCopied = Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("CSV 파일 다운로드 완료: {} -> {} ({} bytes)", urlString, tempFile, bytesCopied);
        } catch (IOException e) {
            log.error("CSV 파일 다운로드 중 오류 발생: {} - {}", urlString, e.getMessage());
            Files.deleteIfExists(tempFile);
            throw e;
        }

        return tempFile;
    }

    public boolean isValidCsvUrl(String csvUrl) {
        if (csvUrl == null || csvUrl.trim().isEmpty()) {
            return false;
        }

        if (!csvUrl.startsWith("https://")) {
            return false;
        }

        return csvUrl.toLowerCase().endsWith(".csv");
    }
}
