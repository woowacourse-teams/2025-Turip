package turip.data.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import turip.data.service.CsvFileService;
import turip.data.service.DataImportService;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final DataImportService dataImportService;
    private final CsvFileService csvFileService;
    private final JdbcTemplate jdbcTemplate;
    @Value("${csv.links.collection-url:}")
    private String csvLinksCollectionUrl;

    @Override
    public void run(String... args) {
        try {
            if (csvLinksCollectionUrl == null || csvLinksCollectionUrl.isBlank()) {
                log.warn("csv.links.collection-url 이 설정되지 않아 DevDataInitializer를 건너뜁니다.");
                return;
            }
            // CSV 파일 링크 모음집 다운로드 및 링크 추출
            List<String> csvUrls = downloadAndExtractCsvLinks();

            if (csvUrls.isEmpty()) {
                log.warn("CSV 링크를 찾을 수 없습니다. CSV 파일 링크 모음집을 확인해주세요.");
                return;
            }

            // CSV 파일들 import 실행
            importCsvFiles(csvUrls);

        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private List<String> downloadAndExtractCsvLinks() {
        List<String> csvUrls = new ArrayList<>();

        try {
            log.info("CSV 파일 링크 모음집 다운로드 시작: {}", csvLinksCollectionUrl);

            // CSV 파일 링크 모음집 다운로드
            Path tempFile = csvFileService.downloadCsvFromUrl(csvLinksCollectionUrl);

            if (!csvFileService.isValidCsvFile(tempFile)) {
                log.error("유효하지 않은 CSV 파일 링크 모음집: {}", csvLinksCollectionUrl);
                return csvUrls;
            }

            // CSV 파일에서 링크들 읽기
            List<String> lines = Files.readAllLines(tempFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    // 꺾쇠 괄호(<>) 제거
                    String cleanUrl = trimmedLine.replaceAll("[<>]", "");

                    // HTTP/HTTPS로 시작하는지 확인
                    if (cleanUrl.startsWith("http://") || cleanUrl.startsWith("https://")) {
                        csvUrls.add(cleanUrl);
                        log.debug("CSV 링크 추가: {}", cleanUrl);
                    } else {
                        log.debug("유효하지 않은 링크 형식 무시: {}", trimmedLine);
                    }
                }
            }

            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);

            log.info("CSV 파일 링크 {}개를 성공적으로 추출했습니다.", csvUrls.size());

        } catch (Exception e) {
            log.error("CSV 파일 링크 모음집 다운로드 및 링크 추출 실패: {}", e.getMessage(), e);
        }

        return csvUrls;
    }

    private void importCsvFiles(List<String> csvUrls) {
        for (String csvUrl : csvUrls) {
            Path tempFile = null;
            try {
                log.info("CSV 파일 import 시작: {}", csvUrl);

                // CSV 파일 다운로드
                tempFile = csvFileService.downloadCsvFromUrl(csvUrl);

                // CSV 파일 검증
                if (csvFileService.isValidCsvFile(tempFile)) {
                    // CSV 데이터 import 실행
                    dataImportService.importCsvData(tempFile.toString());
                    log.info("CSV 파일 import 완료: {}", csvUrl);
                } else {
                    log.warn("유효하지 않은 CSV 파일: {}", csvUrl);
                }


            } catch (Exception e) {
                log.error("CSV 파일 import 실패: {} - {}", csvUrl, e.getMessage(), e);
            } finally {
                try {
                    if (tempFile != null) {
                        Files.deleteIfExists(tempFile);
                    }
                } catch (Exception ex) {
                    log.warn("임시 파일 삭제 실패: {}", ex.getMessage());
                }
            }
        }

        log.info("모든 CSV 파일 import 완료되었습니다.");
    }
}
