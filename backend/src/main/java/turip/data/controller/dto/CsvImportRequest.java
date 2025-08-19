package turip.data.controller.dto;

public record CsvImportRequest(
        String csvUrl,
        String password
) {

}
