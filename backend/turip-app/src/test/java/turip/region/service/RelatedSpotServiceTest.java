package turip.region.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import turip.infrastructure.client.KoreaTourismRelatedSpotClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.infrastructure.client.dto.RelatedSpotResult;
import turip.region.controller.dto.response.RelatedSpotsResponse;
import turip.region.domain.DomesticRegionCategory;

@ExtendWith(MockitoExtension.class)
class RelatedSpotServiceTest {

    @InjectMocks
    private RelatedSpotService relatedSpotService;

    @Mock
    private KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    @DisplayName("지역 카테고리로 연관 관광지를 조회한다")
    @Test
    void findRelatedSpotsByRegionCategory1() {
        // given
        DomesticRegionCategory category = DomesticRegionCategory.SEOUL;

        RelatedSpot spot1 = mock(RelatedSpot.class);
        RelatedSpot spot2 = mock(RelatedSpot.class);
        RelatedSpot spot3 = mock(RelatedSpot.class);

        given(spot1.getRelatedCategoryLargeName()).willReturn("관광지");
        given(spot1.getRelatedSpotName()).willReturn("북촌한옥마을");
        given(spot2.getRelatedCategoryLargeName()).willReturn("관광지");
        given(spot2.getRelatedSpotName()).willReturn("경복궁");
        given(spot3.getRelatedCategoryLargeName()).willReturn("관광지");
        given(spot3.getRelatedSpotName()).willReturn("남산타워");

        // 서울의 3개 시군구 코드에 대해 각각 모킹
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110))
                .willReturn(Mono.just(RelatedSpotResult.success(List.of(spot1))));
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11440))
                .willReturn(Mono.just(RelatedSpotResult.success(List.of(spot2))));
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11140))
                .willReturn(Mono.just(RelatedSpotResult.success(List.of(spot3))));

        // when
        RelatedSpotsResponse response = relatedSpotService.findRelatedSpotsByRegionCategory(category);

        // then
        assertAll(
                () -> assertThat(response.relatedSpots()).hasSize(1),
                () -> assertThat(response.relatedSpots().getFirst().category()).isEqualTo("관광지"),
                () -> assertThat(response.relatedSpots().getFirst().spots())
                        .containsExactlyInAnyOrder("북촌한옥마을", "경복궁", "남산타워")
        );
    }

    @DisplayName("'국내 기타' 카테고리는 예외를 던진다")
    @Test
    void findRelatedSpotsByRegionCategory2() {
        // given
        DomesticRegionCategory category = DomesticRegionCategory.OTHER_DOMESTIC;

        // when & then
        assertThatThrownBy(() -> relatedSpotService.findRelatedSpotsByRegionCategory(category))
                .isInstanceOf(turip.common.exception.custom.IllegalArgumentException.class);
    }

    @DisplayName("외부 API 호출이 실패하면 fallback 데이터를 사용한다")
    @Test
    void findRelatedSpotsByRegionCategory3() {
        // given
        DomesticRegionCategory category = DomesticRegionCategory.SEOUL;

        // 서울의 3개 시군구 코드에 대해 모두 실패 반환
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110))
                .willReturn(Mono.just(RelatedSpotResult.failure()));
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11440))
                .willReturn(Mono.just(RelatedSpotResult.failure()));
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11140))
                .willReturn(Mono.just(RelatedSpotResult.failure()));

        // when
        RelatedSpotsResponse response = relatedSpotService.findRelatedSpotsByRegionCategory(category);

        // then
        assertThat(response.relatedSpots()).isNotEmpty();
    }

    @DisplayName("외부 API 성공했지만 데이터가 비어있으면 fallback 데이터를 사용한다")
    @Test
    void findRelatedSpotsByRegionCategory4() {
        // given
        DomesticRegionCategory category = DomesticRegionCategory.SEOUL;

        // 서울의 3개 시군구 코드에 대해 성공했지만 빈 데이터 반환
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110))
                .willReturn(Mono.just(RelatedSpotResult.success(List.of())));
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11440))
                .willReturn(Mono.just(RelatedSpotResult.success(List.of())));
        given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11140))
                .willReturn(Mono.just(RelatedSpotResult.success(List.of())));

        // when
        RelatedSpotsResponse response = relatedSpotService.findRelatedSpotsByRegionCategory(category);

        // then
        assertThat(response.relatedSpots()).isNotEmpty();
    }
}
