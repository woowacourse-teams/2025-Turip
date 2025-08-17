package turip.data.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
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
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class ProdCsvDataInitializer implements CommandLineRunner {

    private static final String EXECUTION_LOG_FILE = "execution_log.txt";
    private static final String EXECUTION_LOG_PATH = "src/main/resources/";
    private final CsvDataImportService csvDataImportService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            // 실행 로그 파일 초기화
            initializeExecutionLog();

            // 리소스 패턴으로 CSV 찾기 (resources 하위 모든 폴더 포함)
            Resource[] csvResources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:/**/*.csv");

            if (csvResources.length == 0) {
                log.warn("import 할 CSV 파일이 없습니다.");
            } else {
                for (Resource resource : csvResources) {
                    String fileName = resource.getFilename();
                    log.info("import 대상 CSV 파일: {}", fileName);

                    // 이미 실행된 파일인지 확인
                    if (isAlreadyExecuted(fileName)) {
                        log.info("이미 실행된 파일이므로 건너뜁니다: {}", fileName);
                        continue;
                    }

                    // 임시 파일로 복사
                    File tempFile = File.createTempFile("csv_import_", ".csv");
                    tempFile.deleteOnExit();

                    try (InputStream inputStream = resource.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        inputStream.transferTo(outputStream);
                    }

                    // CSV 데이터 import 실행
                    csvDataImportService.importCsvData(tempFile.getAbsolutePath());

                    // 실행 로그 기록 (파일명만)
                    logExecution(fileName);

                    log.info("CSV 파일 import 완료: {}", fileName);
                }

                log.info("모든 CSV 파일 import 완료되었습니다.");
            }

            // CSV import 완료 후 favorite_content_data.sql 실행 (한 번만)
            if (!isSqlAlreadyExecuted()) {
                executeDataSql();
                logSqlExecution();
            } else {
                log.info("favorite_content_data.sql은 이미 실행되었습니다.");
            }

        } catch (Exception e) {
            log.error("CSV import 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void initializeExecutionLog() {
        try {
            Path logFilePath = Paths.get(EXECUTION_LOG_PATH, EXECUTION_LOG_FILE);

            // 디렉토리가 없으면 생성
            if (!Files.exists(logFilePath.getParent())) {
                Files.createDirectories(logFilePath.getParent());
            }

            // 파일이 없으면 생성 (헤더 없음)
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
                log.info("실행 로그 파일이 생성되었습니다: {}", logFilePath);
            }
        } catch (IOException e) {
            log.error("실행 로그 파일 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private boolean isAlreadyExecuted(String fileName) {
        try {
            Path logFilePath = Paths.get(EXECUTION_LOG_PATH, EXECUTION_LOG_FILE);
            if (!Files.exists(logFilePath)) {
                return false;
            }

            List<String> lines = Files.readAllLines(logFilePath, StandardCharsets.UTF_8);
            return lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .anyMatch(line -> line.trim().equals(fileName));
        } catch (IOException e) {
            log.warn("실행 여부 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    private boolean isSqlAlreadyExecuted() {
        try {
            Path logFilePath = Paths.get(EXECUTION_LOG_PATH, EXECUTION_LOG_FILE);
            if (!Files.exists(logFilePath)) {
                return false;
            }

            List<String> lines = Files.readAllLines(logFilePath, StandardCharsets.UTF_8);
            return lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .anyMatch(line -> line.trim().equals("SQL_EXECUTED"));
        } catch (IOException e) {
            log.warn("SQL 실행 여부 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    private void logExecution(String fileName) {
        try {
            Path logFilePath = Paths.get(EXECUTION_LOG_PATH, EXECUTION_LOG_FILE);
            Files.write(logFilePath, (fileName + "\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND);

            log.debug("실행 로그 기록 완료: {}", fileName);
        } catch (IOException e) {
            log.error("실행 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void logSqlExecution() {
        try {
            Path logFilePath = Paths.get(EXECUTION_LOG_PATH, EXECUTION_LOG_FILE);
            Files.write(logFilePath, "SQL_EXECUTED\n".getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND);

            log.debug("SQL 실행 로그 기록 완료");
        } catch (IOException e) {
            log.error("SQL 실행 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
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
