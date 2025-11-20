package turip.favorite.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.auth.resolver.AuthMember;
import turip.common.exception.ErrorResponse;
import turip.content.controller.dto.response.content.ContentsDetailWithLoadableResponse;
import turip.favorite.controller.dto.request.FavoriteContentRequest;
import turip.favorite.controller.dto.response.FavoriteContentResponse;
import turip.favorite.service.FavoriteContentService;
import turip.member.domain.Account;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites/contents")
@Tag(name = "FavoriteContent", description = "컨텐츠 찜 API")
public class FavoriteContentController {

    private final FavoriteContentService favoriteContentService;

    @Operation(
            summary = "컨텐츠 찜 생성 api",
            description = "컨텐츠를 찜한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteContentResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "컨텐츠 찜 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "createdAt": "2025-08-06",
                                                "memberId": 1,
                                                "content": {
                                                    "id": 1,
                                                    "title": "나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그",
                                                    "url": "https://www.youtube.com/watch?v=U7vwpgZlD6Q",
                                                    "uploadedDate": "2025-07-01",
                                                    "city": {
                                                        "name": "부산"
                                                    },
                                                    "creator": {
                                                        "id": 1,
                                                        "channelName": "연수연",
                                                        "profileImage": "https://yt3.googleusercontent.com/EMvavcwV96_NkCYm4V8TZIrsytHaiS2AaxS_goqR57WP7kn36qQY92Ujex8JUbBWGQ7P5VY0DA=s160-c-k-c0x00ffffff-no-rj"
                                                    },
                                                    "isFavorite": true
                                                }
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
                            examples = {
                                    @ExampleObject(
                                            name = "content_not_found",
                                            summary = "컨텐츠를 찾을 수 없음",
                                            value = """
                                                    {
                                                        "tag": "CONTENT_NOT_FOUND"
                                                    }
                                                    """
                                    )
                            }
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
                                            summary = "이미 찜 한 컨텐츠",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_CONTENT_CONFLICT"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoriteContentResponse> create(
            @Parameter(hidden = true) @AuthMember Account account,
            @RequestBody FavoriteContentRequest request) {
        FavoriteContentResponse response = favoriteContentService.create(request, account);
        return ResponseEntity.created(URI.create("/favorites/contents/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "내 컨텐츠 찜 목록 조회 api",
            description = "내가 찜한 컨텐츠 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentsDetailWithLoadableResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "내가 찜한 컨텐츠 목록 조회 성공",
                                    value = """
                                            {
                                                "contents": [
                                                    {
                                                        "content": {
                                                            "id": 1,
                                                            "creator": {
                                                                "id": 1,
                                                                "channelName": "연수연",
                                                                "profileImage": "https://yt3.googleusercontent.com/EMvavcwV96_NkCYm4V8TZIrsytHaiS2AaxS_goqR57WP7kn36qQY92Ujex8JUbBWGQ7P5VY0DA=s160-c-k-c0x00ffffff-no-rj"
                                                            },
                                                            "title": "나혼자 기차 타고 부산 여행 vlog 🌊 | 당일치기 쌉가능한 여행코스 💌 , 200% 만족한 광안리 숙소 🏠, 부산 토박이의 단골집 추천까지,,💛 | 3박4일 부산 브이로그",
                                                            "url": "https://www.youtube.com/watch?v=U7vwpgZlD6Q",
                                                            "uploadedDate": "2025-07-01",
                                                            "city": {
                                                                "name": "부산"
                                                            }
                                                        },
                                                        "tripDuration": {
                                                            "nights": 2,
                                                            "days": 3
                                                        },
                                                        "tripPlaceCount": 21
                                                    }
                                                ],
                                                "loadable": false
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
                            examples = {
                                    @ExampleObject(
                                            name = "invalid device-fid",
                                            summary = "올바르지 않은 device-fid",
                                            value = """
                                                    {
                                                        "tag": "MEMBER_NOT_FOUND"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ContentsDetailWithLoadableResponse> readMyFavoriteContents(
            @Parameter(hidden = true) @AuthMember Account account,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastContentId
    ) {
        ContentsDetailWithLoadableResponse response = favoriteContentService.findMyFavoriteContents(account, pageSize,
                lastContentId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "컨텐츠 찜 삭제 api",
            description = "컨텐츠 찜을 취소한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "content_not_found",
                                            summary = "컨텐츠를 찾을 수 없음",
                                            value = """
                                                    {
                                                        "tag": "CONTENT_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "member_not_found",
                                            summary = "존재하지 않는 사용자",
                                            value = """
                                                    {
                                                        "tag": "MEMBER_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "not_favorite",
                                            summary = "찜하지 않은 컨텐츠",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_CONTENT_NOT_FOUND"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @AuthMember Account account,
            @RequestParam(name = "contentId") Long contentId) {
        favoriteContentService.remove(account, contentId);
        return ResponseEntity.noContent().build();
    }
}
