package turip.data.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.data.service.CsvDataImportService;

@Slf4j
@RestController
@Profile("!test")
@RequiredArgsConstructor
@RequestMapping("/api/csv")
public class DataImportController {

    private final CsvDataImportService csvDataImportService;

    @Value("${csv.import.password:}")
    private String csvImportPassword;

    @PostMapping("/import-body")
    public ResponseEntity<String> importCsvDataFromBody(
            @RequestBody CsvImportRequest request) {

        return processCsvImport(request.csvUrl(), request.password());
    }

    private ResponseEntity<String> processCsvImport(String csvUrl, String password) {
        // 비밀번호 검증
        if (!csvImportPassword.equals(password)) {
            log.warn("잘못된 비밀번호로 CSV import 시도: {}", csvUrl);
            return ResponseEntity.status(401).body("인증 실패: 잘못된 비밀번호입니다.");
        }

        // CSV URL 검증
        if (!isValidCsvUrl(csvUrl)) {
            log.warn("잘못된 CSV URL 형식: {}", csvUrl);
            return ResponseEntity.badRequest().body("잘못된 CSV URL 형식입니다. HTTPS URL이어야 하며 .csv 확장자를 가져야 합니다.");
        }

        try {
            log.info("CSV import 시작: {}", csvUrl);

            // CSV 파일 다운로드
            Path tempFile = downloadCsvFromUrl(csvUrl);

            // CSV 파일 검증
            if (!isValidCsvFile(tempFile)) {
                Files.deleteIfExists(tempFile);
                return ResponseEntity.badRequest().body("유효하지 않은 CSV 파일입니다.");
            }

            // CSV 데이터 import 실행
            csvDataImportService.importCsvData(tempFile.toString());

            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);

            log.info("CSV 데이터 import 완료: {}", csvUrl);
            return ResponseEntity.ok("CSV 데이터 import가 완료되었습니다.");

        } catch (IOException e) {
            log.error("CSV 파일 다운로드 실패: {} - {}", csvUrl, e.getMessage(), e);
            return ResponseEntity.badRequest().body("CSV 파일 다운로드 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("CSV 데이터 import 실패: {} - {}", csvUrl, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("CSV 데이터 import 실패: " + e.getMessage());
        }
    }

    private boolean isValidCsvUrl(String csvUrl) {
        if (csvUrl == null || csvUrl.trim().isEmpty()) {
            return false;
        }

        // HTTPS URL 인지 확인
        if (!csvUrl.startsWith("https://")) {
            return false;
        }

        // CSV 파일 확장자인지 확인
        return csvUrl.toLowerCase().endsWith(".csv");
    }

    private boolean isValidCsvFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return false;
        }

        // 파일 크기 검증 (최소 10바이트)
        long fileSize = Files.size(filePath);
        if (fileSize < 10) {
            log.warn("CSV 파일이 너무 작습니다: {} bytes", fileSize);
            return false;
        }

        // 파일 확장자 검증
        String fileName = filePath.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".csv")) {
            log.warn("CSV 파일 확장자가 아닙니다: {}", fileName);
            return false;
        }

        return true;
    }

    private Path downloadCsvFromUrl(String urlString) throws IOException {
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

    // Request DTO
    public record CsvImportRequest(String csvUrl, String password) {
    }
} 
