package turip.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.controller.dto.response.MigrationAvailabilityResponse;
import turip.account.domain.Guest;
import turip.account.service.GuestService;
import turip.auth.resolver.AuthGuest;
import turip.common.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guests")
@Tag(name = "Guest", description = "게스트 API")
public class GuestController {

    private final GuestService guestService;

    @Operation(
            summary = "게스트 마이그레이션 가능 여부 조회 api",
            description = "게스트 계정에 콘텐츠 찜, 장소 찜, 커스텀 찜 폴더가 존재하는지 확인한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MigrationAvailabilityResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 마이그레이션 가능 여부 조회",
                                    value = """
                                            {
                                                "availability": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "device-fid required",
                                    summary = "요청 헤더에 device-fid가 존재하지 않는 경우",
                                    value = """
                                            {
                                            	"tag": "DEVICE_FID_REQUIRED",
                                            	"message": "요청 헤더에 device_fid가 존재하지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/migration/availability")
    public ResponseEntity<MigrationAvailabilityResponse> readMigrationAvailability(
            @Parameter(hidden = true) @AuthGuest Guest guest) {
        MigrationAvailabilityResponse response = guestService.checkMigrationAvailability(guest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "게스트 탈퇴 api",
            description = "게스트를 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "device-fid required",
                                    summary = "요청 헤더에 device-fid가 존재하지 않는 경우",
                                    value = """
                                            {
                                            	"tag": "DEVICE_FID_REQUIRED",
                                            	"message": "요청 헤더에 device_fid가 존재하지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@Parameter(hidden = true) @AuthGuest Guest guest) {
        guestService.delete(guest);
        return ResponseEntity.noContent().build();
    }
}
