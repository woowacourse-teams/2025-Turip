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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.Account;
import turip.auth.resolver.AuthAccount;
import turip.common.exception.ErrorResponse;
import turip.content.controller.dto.response.content.ContentsDetailWithLoadableResponse;
import turip.favorite.controller.dto.request.FavoriteContentRequest;
import turip.favorite.controller.dto.response.FavoriteContentCountResponse;
import turip.favorite.controller.dto.response.FavoriteContentDetailsWithLoadableResponse;
import turip.favorite.controller.dto.response.FavoriteContentResponse;
import turip.favorite.service.FavoriteContentService;
import turip.favorite.service.dto.FavoriteContentWithLoadableResult;

@RestController
@RequiredArgsConstructor
@Tag(name = "Bookmark", description = "북마크 API")
public class FavoriteContentController {

    private final FavoriteContentService favoriteContentService;

    @Operation(
            summary = "북마크 생성 api",
            description = "컨텐츠를 북마크한다."
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
                                    summary = "북마크 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "createdAt": "2025-08-06",
                                                "accountId": 1,
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
                                                    "isBookmarked": true
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "access token expired",
                                            summary = "만료된 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_EXPIRED",
                                                    	"message": "access token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature access token",
                                            summary = "서명값이 올바르지 않은 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_SIGNATURE_INVALID",
                                                    	"message": "access token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "unauthorized",
                                            summary = "알 수 없는 이유로 인증 실패",
                                            value = """
                                                    {
                                                    	"tag": "UNAUTHORIZED",
                                                    	"message": "토큰 기반 인증에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                                        "tag": "CONTENT_NOT_FOUND",
                                                        "message": "컨텐츠를 찾을 수 없습니다."
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
                                                        "tag": "FAVORITE_CONTENT_CONFLICT",
                                                        "message": "이미 찜한 컨텐츠입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/api/v1/bookmarks")
    public ResponseEntity<FavoriteContentResponse> create(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestBody FavoriteContentRequest request) {
        FavoriteContentResponse response = favoriteContentService.create(request, account);
        return ResponseEntity.created(URI.create("/api/v1/bookmarks/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "내 북마크 목록 조회 api v1",
            description = "내가 북마크한 콘텐츠 목록을 조회한다."
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
                                                            "isBookmarked": true
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
                    responseCode = "401",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "access token expired",
                                            summary = "만료된 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_EXPIRED",
                                                    	"message": "access token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature access token",
                                            summary = "서명값이 올바르지 않은 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_SIGNATURE_INVALID",
                                                    	"message": "access token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "unauthorized",
                                            summary = "알 수 없는 이유로 인증 실패",
                                            value = """
                                                    {
                                                    	"tag": "UNAUTHORIZED",
                                                    	"message": "토큰 기반 인증에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/api/v1/bookmarks")
    public ResponseEntity<ContentsDetailWithLoadableResponse> readMyFavoriteContentsV1(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastId
    ) {
        FavoriteContentWithLoadableResult result = favoriteContentService.findMyFavoriteContents(account, pageSize,
                lastId);
        ContentsDetailWithLoadableResponse response = ContentsDetailWithLoadableResponse.from(result);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "내 북마크 목록 조회 api v2",
            description = "내가 북마크한 콘텐츠 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteContentDetailsWithLoadableResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "내가 찜한 컨텐츠 목록 조회 성공",
                                    value = """
                                            {
                                            	"bookmarks" : [
                                            	  {
                                            		  "id": 1,
                                            		  "createdAt": "2025-08-06",
                                            		  "accountId": 1,
                                            	    "content": {
                                            	      "id": 1,
                                            	      "title": "느좋 감성 대구 여행 어쩌구저쩌구",
                                            	      "url": "https://youtube.com/watch?v=abc123",
                                            	      "uploadedDate": "2024-04-21",
                                            	      "city": {
                                            	        "name": "속초"
                                            	      },
                                            	      "creator": {
                                            	        "id": 10,
                                            	        "channelName": "여행하는 뭉치",
                                            	        "profileImage": "http://turip.com/static/youtuber1"
                                            	      },
                                            	      "isBookmarked": false
                                                  }
                                            	    "tripDuration": {
                                            	      "nights": 2,
                                            	      "days": 3
                                            	    },
                                            	    "tripPlaceCount" : 14
                                            	  }
                                            	],
                                            	"loadable" : true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "access token expired",
                                            summary = "만료된 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_EXPIRED",
                                                    	"message": "access token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature access token",
                                            summary = "서명값이 올바르지 않은 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_SIGNATURE_INVALID",
                                                    	"message": "access token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "unauthorized",
                                            summary = "알 수 없는 이유로 인증 실패",
                                            value = """
                                                    {
                                                    	"tag": "UNAUTHORIZED",
                                                    	"message": "토큰 기반 인증에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/api/v2/bookmarks")
    public ResponseEntity<FavoriteContentDetailsWithLoadableResponse> readMyFavoriteContentsV2(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastId
    ) {
        FavoriteContentWithLoadableResult result = favoriteContentService.findMyFavoriteContents(account, pageSize,
                lastId);
        FavoriteContentDetailsWithLoadableResponse response = FavoriteContentDetailsWithLoadableResponse.from(result);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "내 콘텐츠 찜(북마크) 수 조회 api",
            description = "콘텐츠 찜(북마크)이 몇 개인지 반환한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteContentCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "북마크 수 조회 성공",
                                    value = """
                                            {
                                                "count": 13
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "access token expired",
                                            summary = "만료된 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_EXPIRED",
                                                    	"message": "access token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature access token",
                                            summary = "서명값이 올바르지 않은 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_SIGNATURE_INVALID",
                                                    	"message": "access token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "unauthorized",
                                            summary = "알 수 없는 이유로 인증 실패",
                                            value = """
                                                    {
                                                    	"tag": "UNAUTHORIZED",
                                                    	"message": "토큰 기반 인증에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/api/v1/bookmarks/count")
    public ResponseEntity<FavoriteContentCountResponse> readBookmarkCount(
            @Parameter(hidden = true) @AuthAccount Account account) {
        FavoriteContentCountResponse response = favoriteContentService.countByAccount(account);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "북마크 삭제 api",
            description = "컨텐츠 북마크를 취소한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "access token expired",
                                            summary = "만료된 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_EXPIRED",
                                                    	"message": "access token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature access token",
                                            summary = "서명값이 올바르지 않은 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_SIGNATURE_INVALID",
                                                    	"message": "access token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "unauthorized",
                                            summary = "알 수 없는 이유로 인증 실패",
                                            value = """
                                                    {
                                                    	"tag": "UNAUTHORIZED",
                                                    	"message": "토큰 기반 인증에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                                        "tag": "CONTENT_NOT_FOUND",
                                                        "message": "컨텐츠를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "not_favorite",
                                            summary = "찜하지 않은 컨텐츠",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_CONTENT_NOT_FOUND",
                                                        "message": "찜한 컨텐츠를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping("/api/v1/bookmarks")
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam(name = "contentId") Long contentId) {
        favoriteContentService.remove(account, contentId);
        return ResponseEntity.noContent().build();
    }
}
