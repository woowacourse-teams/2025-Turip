package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.common.exception.custom.BadRequestException;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchClient;
import turip.place.domain.PlaceSearchProvider;
import turip.place.domain.PlaceSearchType;

class AdminPlaceServiceTest {

    private AdminPlaceService adminPlaceService;
    private PlaceSearchClient kakaoClient;
    private PlaceSearchClient googleClient;

    @BeforeEach
    void setUp() {
        kakaoClient = mock(PlaceSearchClient.class);
        googleClient = mock(PlaceSearchClient.class);

        when(kakaoClient.supports(PlaceSearchType.DOMESTIC)).thenReturn(true);
        when(googleClient.supports(PlaceSearchType.OVERSEAS)).thenReturn(true);

        adminPlaceService = new AdminPlaceService(List.of(kakaoClient, googleClient));
    }

    @Test
    @DisplayName("검색 타입이 DOMESTIC일 때 카카오 클라이언트를 호출한다.")
    void searchPlaces_withDomesticType_callsKakaoClient() {
        // given
        String query = "test";
        PlaceSearchResponse mockResponse = new PlaceSearchResponse(PlaceSearchProvider.KAKAO, Collections.emptyList());
        when(kakaoClient.search(query)).thenReturn(mockResponse);

        // when
        PlaceSearchResponse actualResponse = adminPlaceService.searchPlaces(query, PlaceSearchType.DOMESTIC);

        // then
        assertThat(actualResponse.provider()).isEqualTo(PlaceSearchProvider.KAKAO);
        verify(kakaoClient, times(1)).search(query);
        verify(googleClient, never()).search(anyString());
    }

    @Test
    @DisplayName("검색 타입이 OVERSEAS일 때 구글 클라이언트를 호출한다.")
    void searchPlaces_withOverseasType_callsGoogleClient() {
        // given
        String query = "test";
        PlaceSearchResponse mockResponse = new PlaceSearchResponse(PlaceSearchProvider.GOOGLE, Collections.emptyList());
        when(googleClient.search(query)).thenReturn(mockResponse);

        // when
        PlaceSearchResponse actualResponse = adminPlaceService.searchPlaces(query, PlaceSearchType.OVERSEAS);

        // then
        assertThat(actualResponse.provider()).isEqualTo(PlaceSearchProvider.GOOGLE);
        verify(googleClient, times(1)).search(query);
        verify(kakaoClient, never()).search(anyString());
    }

    @Test
    @DisplayName("지원하지 않는 검색 타입일 경우 BadRequestException을 던진다.")
    void searchPlaces_withUnsupportedType_throwsException() {
        // given
        String query = "test";
        AdminPlaceService serviceWithNoSupportingClients = new AdminPlaceService(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> serviceWithNoSupportingClients.searchPlaces(query, PlaceSearchType.DOMESTIC))
                .isInstanceOf(BadRequestException.class);
    }
}
