package turip.data.controller.dto.request;

public record CsvImportRequest(
        String csvUrl,
        String password
) {

}
