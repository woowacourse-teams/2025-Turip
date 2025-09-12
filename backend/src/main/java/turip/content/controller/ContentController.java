package turip.content.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.auth.AuthMember;
import turip.auth.MemberResolvePolicy;
import turip.common.exception.ErrorResponse;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.WeeklyPopularFavoriteContentsResponse;
import turip.content.controller.dto.response.todo.ContentResponse;
import turip.content.controller.dto.response.todo.ContentsWithLoadableResponse;
import turip.content.service.ContentService;
import turip.member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
@Tag(name = "Content", description = "컨텐츠 API")
public class ContentController {

    private final ContentService contentService;

    @Operation(
            summary = "지역 카테고리 별 컨텐츠 수 조회 api",
            description = "지역 카테고리 기반 컨텐츠 수를 조회한다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 컨텐츠 수 조회",
                                    value = """
                                            {
                                                "count": 12
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
                                    name = "failure",
                                    summary = "지역 카테고리가 올바르지 않음",
                                    value = """
                                            {
                                                "message": "지역 카테고리가 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/count")
    public ResponseEntity<ContentCountResponse> readCountByRegionCategory(
            @RequestParam(name = "regionCategory") String regionCategory) {
        ContentCountResponse response = contentService.countByRegionCategory(regionCategory);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "검색 결과 컨텐츠 수 조회 api",
            description = "키워드 기반 컨텐츠 수 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 컨텐츠 수 조회",
                                    value = """
                                            {
                                                "count": 12
                                            }
                                            """
                            )
                    ))
    })
    @GetMapping("/keyword/count")
    public ResponseEntity<ContentCountResponse> readCountByKeyword(
            @RequestParam(name = "keyword") String keyword) {
        ContentCountResponse response = contentService.countByKeyword(keyword);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "지역 카테고리 별 컨텐츠 목록 조회 api",
            description = "지역 카테고리 기반 컨텐츠 목록 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentsWithLoadableResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 컨텐츠 목록 조회",
                                    value = """
                                            {
                                                "contents": [
                                                    {
                                                        "content": {
                                                            "id": 2,
                                                            "creator": {
                                                                "id": 2,
                                                                "channelName": "하찬투어 hachantour",
                                                                "profileImage": "https://yt3.googleusercontent.com/xMc7FcCl689p_ymaijuY5WOwX9DeHaZ_WTnRHb8UajggQotOO8Bxd0P7cqsYYfubotgjlh4Qfw=s160-c-k-c0x00ffffff-no-rj"
                                                            },
                                                            "title": "동선낭비없는 부산 3박4일 코스 대마도 당일치기 꿀팁 반드시 가봐야 할 여행지 국내 단 하나뿐인 스카이캡슐 오륙도해맞이공원 흰여울문화마을 감천문화마을 해동용궁사 송도케이블카",
                                                            "url": "https://www.youtube.com/watch?v=1he5ed8Y5TA",
                                                            "uploadedDate": "2025-07-10",
                                                            "city": "부산"
                                                        },
                                                        "tripDuration": {
                                                            "nights": 3,
                                                            "days": 4
                                                        },
                                                        "tripPlaceCount": 22
                                                    },
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
                                                            "city": "부산"
                                                        },
                                                        "tripDuration": {
                                                            "nights": 2,
                                                            "days": 3
                                                        },
                                                        "tripPlaceCount": 21
                                                    }
                                                ],
                                                "loadable": false,
                                                "regionCategoryName": "부산"
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
                                    name = "failure",
                                    summary = "지역 카테고리가 올바르지 않음",
                                    value = """
                                            {
                                                "message": "지역 카테고리가 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ContentsWithLoadableResponse> readContentsByRegionCategory(
            @RequestParam(name = "regionCategory") String regionCategory,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastContentId
    ) {
        ContentsWithLoadableResponse response = contentService.findContentsByRegionCategory(regionCategory,
                pageSize, lastContentId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "검색 결과 조회 api",
            description = "키워드 기반 컨텐츠 목록 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentsWithLoadableResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 컨텐츠 목록 조회",
                                    value = """
                                            {
                                                "contents": [
                                                    {
                                                        "content": {
                                                            "id": 2,
                                                            "creator": {
                                                                "id": 2,
                                                                "channelName": "하찬투어 hachantour",
                                                                "profileImage": "https://yt3.googleusercontent.com/xMc7FcCl689p_ymaijuY5WOwX9DeHaZ_WTnRHb8UajggQotOO8Bxd0P7cqsYYfubotgjlh4Qfw=s160-c-k-c0x00ffffff-no-rj"
                                                            },
                                                            "title": "동선낭비없는 부산 3박4일 코스 대마도 당일치기 꿀팁 반드시 가봐야 할 여행지 국내 단 하나뿐인 스카이캡슐 오륙도해맞이공원 흰여울문화마을 감천문화마을 해동용궁사 송도케이블카",
                                                            "url": "https://www.youtube.com/watch?v=1he5ed8Y5TA",
                                                            "uploadedDate": "2025-07-10",
                                                            "city": {
                                                                "name": "부산"
                                                            }
                                                        },
                                                        "tripDuration": {
                                                            "nights": 3,
                                                            "days": 4
                                                        },
                                                        "tripPlaceCount": 22
                                                    },
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
            )
    })
    @GetMapping("/keyword")
    public ResponseEntity<ContentsWithLoadableResponse> readContentsByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastContentId
    ) {
        ContentsWithLoadableResponse response = contentService.searchContentsByKeyword(keyword, pageSize,
                lastContentId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "컨텐츠, 찜 여부 조회 api",
            description = "id 기반 컨텐츠, 찜 여부 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 컨텐츠 조회",
                                    value = """
                                            {
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
                                                "isFavorite": false
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
                                    summary = "id에 대한 컨텐츠가 존재하지 않음",
                                    value = """
                                            {
                                                "message": "컨텐츠를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentResponse> readContent(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.CREATE_IF_ABSENT) Member member,
            @PathVariable Long contentId
    ) {
        ContentResponse response = contentService.getContentWithFavoriteStatus(contentId, member);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주간 인기 찜 컨텐츠 조회 api",
            description = "지난 주 찜 수가 많은 컨텐츠 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WeeklyPopularFavoriteContentsResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 주간 인기 컨텐츠 조회",
                                    value = """
                                            {
                                              "contents": [
                                                {
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
                                                    "isFavorite": false
                                                  },
                                                  "tripDuration": {
                                                    "nights": 2,
                                                    "days": 3
                                                  }
                                                },
                                                {
                                                  "content": {
                                                    "id": 2,
                                                    "title": "커피 냄새가 솔솔 나는 대전 2박 3일 브이로그",
                                                    "url": "https://youtube.com/watch?v=def456",
                                                    "uploadedDate": "2025-07-21",
                                                    "city": {
                                                      "name": "대전"
                                                    },
                                                    "creator": {
                                                      "id": 11,
                                                      "channelName": "여행하는 하루",
                                                      "profileImage": "http://turip.com/static/youtuber2"
                                                    },
                                                    "isFavorite": true
                                                  },
                                                  "tripDuration": {
                                                    "nights": 1,
                                                    "days": 2
                                                  }
                                                }
                                              ]
                                            }
                                            
                                            """
                            )
                    )

            )
    })
    @GetMapping("/popular/favorites")
    public ResponseEntity<WeeklyPopularFavoriteContentsResponse> readWeeklyPopularFavoriteContents(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.CREATE_IF_ABSENT) Member member,
            @RequestParam("size") int topContentSize) {
        WeeklyPopularFavoriteContentsResponse weeklyPopularFavoriteContents = contentService.findWeeklyPopularFavoriteContents(
                member, topContentSize);
        return ResponseEntity.ok(weeklyPopularFavoriteContents);
    }
}
