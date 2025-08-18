package turip.data.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import turip.data.service.CsvFileService;

@Slf4j
@RestController
@Profile("!test")
@RequiredArgsConstructor
@RequestMapping("/api/csv")
public class DataImportController {

    private final CsvDataImportService csvDataImportService;
    private final CsvFileService csvFileService;

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
        if (!csvFileService.isValidCsvUrl(csvUrl)) {
            log.warn("잘못된 CSV URL 형식: {}", csvUrl);
            return ResponseEntity.badRequest().body("잘못된 CSV URL 형식입니다. HTTPS URL이어야 하며 .csv 확장자를 가져야 합니다.");
        }

        try {
            log.info("CSV import 시작: {}", csvUrl);

            // CSV 파일 다운로드
            Path tempFile = csvFileService.downloadCsvFromUrl(csvUrl);

            // CSV 파일 검증
            if (!csvFileService.isValidCsvFile(tempFile)) {
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

    // Request DTO
    public record CsvImportRequest(String csvUrl, String password) {
    }
} 
