package turip.favoritefolder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.exception.ErrorResponse;
import turip.favoritefolder.controller.dto.request.FavoriteFolderRequest;
import turip.favoritefolder.controller.dto.response.FavoriteFolderResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favoritefolder.service.FavoriteFolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-folders")
@Tag(name = "FavoriteFolder", description = "장소 찜 폴더 API")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;

    @Operation(
            summary = "장소 찜 폴더 생성 api",
            description = "장소 찜 폴더를 생성한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "memberId": 1,
                                                "name": "뭉치가 가고싶은 맛집들",
                                                "isDefault": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "already_favorite",
                                            summary = "같은 이름의 폴더가 이미 존재하는 경우",
                                            value = """
                                                    {
                                                        "message": "중복된 폴더 이름이 존재합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoriteFolderResponse> create(@RequestHeader("device-fid") String deviceFid,
                                                         @RequestBody FavoriteFolderRequest request) {
        FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, deviceFid);
        return ResponseEntity.created(URI.create("/favorite-folders/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "장소 찜 폴더 조회 api",
            description = "특정 회원의 장소 찜 폴더 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFoldersWithPlaceCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 조회 성공",
                                    value = """
                                            {
                                                "favoriteFolders": [
                                                    {
                                                        "id": 5,
                                                        "memberId": 8,
                                                        "name": "기본 폴더",
                                                        "isDefault": false,
                                                        "placeCount": 0
                                                    },
                                                    {
                                                        "id": 6,
                                                        "memberId": 8,
                                                        "name": "뭉치가 가고싶은 맛집들",
                                                        "isDefault": false,
                                                        "placeCount": 0
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<FavoriteFoldersWithPlaceCountResponse> readAllByMember(
            @RequestHeader("device-fid") String deviceFid) {
        FavoriteFoldersWithPlaceCountResponse response = favoriteFolderService.findAllByDeviceFid(deviceFid);
        return ResponseEntity.ok(response);
    }
}
