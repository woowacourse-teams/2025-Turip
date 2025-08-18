package turip.data.config;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import turip.data.service.CsvDataImportService;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
public class DevDataInitializer implements CommandLineRunner {

    // CSV 파일 링크 상수 정의
    private static final String CSV_URL_1 = "https://example.com/data1.csv";
    private static final String CSV_URL_2 = "https://example.com/data2.csv";
    private static final String CSV_URL_3 = "https://example.com/data3.csv";
    private final CsvDataImportService csvDataImportService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            // CSV 파일들 import 실행
            importCsvFiles();

            // CSV import 완료 후 favorite_content_data.sql 실행
            executeDataSql();

        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void importCsvFiles() {
        String[] csvUrls = {CSV_URL_1, CSV_URL_2, CSV_URL_3};

        for (String csvUrl : csvUrls) {
            try {
                log.info("CSV 파일 import 시작: {}", csvUrl);

                // CSV 파일 다운로드
                Path tempFile = downloadCsvFromUrl(csvUrl);

                // CSV 파일 검증
                if (isValidCsvFile(tempFile)) {
                    // CSV 데이터 import 실행
                    csvDataImportService.importCsvData(tempFile.toString());
                    log.info("CSV 파일 import 완료: {}", csvUrl);
                } else {
                    log.warn("유효하지 않은 CSV 파일: {}", csvUrl);
                }

                // 임시 파일 삭제
                Files.deleteIfExists(tempFile);

            } catch (Exception e) {
                log.error("CSV 파일 import 실패: {} - {}", csvUrl, e.getMessage(), e);
            }
        }

        log.info("모든 CSV 파일 import 완료되었습니다.");
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

    private void executeDataSql() {
        try {
            log.info("favorite_content_data.sql 실행을 시작합니다.");

            // favorite_content_data.sql 파일 읽기
            Resource dataSqlResource = new PathMatchingResourcePatternResolver()
                    .getResource("classpath:favorite_content_data.sql");

            if (!dataSqlResource.exists()) {
                log.warn("favorite_content_data.sql 파일을 찾을 수 없습니다.");
                return;
            }

            String sqlContent = StreamUtils.copyToString(
                    dataSqlResource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            // SQL 문장들을 세미콜론으로 분리하여 실행
            String[] sqlStatements = sqlContent.split(";");

            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        jdbcTemplate.execute(sql);
                        log.debug("SQL 실행 완료: {}", sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        log.error("SQL 실행 중 오류 발생: {}", e.getMessage());
                        log.error("실행 실패한 SQL: {}", sql);
                    }
                }
            }

            log.info("favorite_content_data.sql 실행이 완료되었습니다.");

        } catch (Exception e) {
            log.error("favorite_content_data.sql 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
