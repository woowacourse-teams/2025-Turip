package turip.csv.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import turip.csv.service.CsvDataImportService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvDataInitializer implements CommandLineRunner {

    private final CsvDataImportService csvDataImportService;

    @Override
    public void run(String... args) {
        try {
            // 리소스 패턴으로 CSV 찾기 (resources 하위 모든 폴더 포함)
            Resource[] csvResources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:/**/*.csv");

            if (csvResources.length == 0) {
                log.warn("import 할 CSV 파일이 없습니다.");
                return;
            }

            log.info("찾은 CSV 파일 개수: {}", csvResources.length);
            for (Resource resource : csvResources) {
                log.info("import 대상 CSV 파일: {}", resource.getFilename());

                // 임시 파일로 복사
                File tempFile = File.createTempFile("csv_import_", ".csv");
                tempFile.deleteOnExit();

                try (InputStream inputStream = resource.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    inputStream.transferTo(outputStream);
                }

                // CSV 데이터 import 실행
                csvDataImportService.importCsvData(tempFile.getAbsolutePath());
                log.info("CSV 파일 import 완료: {}", resource.getFilename());
            }

            log.info("모든 CSV 파일 import 완료되었습니다.");

        } catch (Exception e) {
            log.error("CSV import 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
