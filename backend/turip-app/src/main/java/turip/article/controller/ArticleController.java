package turip.article.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.article.controller.dto.response.ArticleResponse;
import turip.article.controller.dto.response.ArticlesResponse;
import turip.article.service.ArticleService;
import turip.common.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
@Tag(name = "Article", description = "아티클 API")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(
            summary = "아티클 목록 조회 api",
            description = "공개된 아티클 목록을 displayOrder 오름차순 커서 페이징으로 조회한다. 인증 불필요."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArticlesResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 아티클 목록 조회",
                                    value = """
                                            {
                                                "articles": [
                                                    {
                                                        "id": 1,
                                                        "title": "전주 한옥마을 산책 코스",
                                                        "subtitle": "골목마다 숨은 전주의 매력",
                                                        "thumbnailUrl": "https://turip.com/static/thumbnail-1.png",
                                                        "createdAt": "2026-09-01T12:00:00",
                                                        "tags": ["전주", "한옥마을"],
                                                        "author": {
                                                            "id": 1,
                                                            "nickname": "튜립"
                                                        }
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
                            examples = @ExampleObject(
                                    name = "failure",
                                    summary = "lastId에 해당하는 아티클이 존재하지 않음",
                                    value = """
                                            {
                                                "tag": "ARTICLE_NOT_FOUND",
                                                "message": "아티클을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ArticlesResponse> readArticles(
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "lastId", required = false) Long lastId
    ) {
        ArticlesResponse response = articleService.findArticles(size, lastId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "아티클 상세 조회 api",
            description = "id로 공개된 아티클 상세 정보를 조회한다. 인증 불필요."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArticleResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 아티클 상세 조회",
                                    value = """
                                            {
                                                "id": 1,
                                                "title": "전주 한옥마을 산책 코스",
                                                "subtitle": "골목마다 숨은 전주의 매력",
                                                "content": "본문 마크다운 문자열",
                                                "thumbnailUrl": "https://turip.com/static/thumbnail-1.png",
                                                "tags": ["전주", "한옥마을"],
                                                "author": {
                                                    "id": 1,
                                                    "nickname": "튜립"
                                                },
                                                "places": [],
                                                "createdAt": "2026-09-01T12:00:00",
                                                "updatedAt": "2026-09-01T12:00:00"
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
                                    summary = "id에 대한 아티클이 존재하지 않거나 비공개임",
                                    value = """
                                            {
                                                "tag": "ARTICLE_NOT_FOUND",
                                                "message": "아티클을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> readArticle(@PathVariable Long articleId) {
        ArticleResponse response = articleService.getArticle(articleId);
        return ResponseEntity.ok(response);
    }
}
