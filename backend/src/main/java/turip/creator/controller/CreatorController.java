package turip.creator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.creator.controller.dto.response.CreatorResponse;
import turip.creator.service.CreatorService;
import turip.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/creators")
@Tag(name = "Creator", description = "크리에이터 API")
public class CreatorController {

    private final CreatorService creatorService;

    @Operation(
            summary = "크리에이터 조회 api",
            description = "id를 기반으로 크리에이터를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatorResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 크리에이터 조회",
                                    value = """
                                            {
                                                "id": 1,
                                                "channelName": "연수연",
                                                "profileImage": "https://yt3.googleusercontent.com/EMvavcwV96_NkCYm4V8TZIrsytHaiS2AaxS_goqR57WP7kn36qQY92Ujex8JUbBWGQ7P5VY0DA=s160-c-k-c0x00ffffff-no-rj"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "failure",
                                    summary = "크리에이터가 존재하지 않음",
                                    value = """
                                            {
                                                "message": "크리에이터를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponse> readCreatorById(@PathVariable Long id) {
        CreatorResponse response = creatorService.getById(id);
        return ResponseEntity.ok(response);
    }
}
