package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.content.domain.Content;
import turip.content.repository.ContentPlaceRepository;
import turip.content.repository.ContentRepository;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.place.domain.Category;
import turip.place.domain.Place;
import turip.place.domain.PlaceCategory;
import turip.place.repository.CategoryRepository;
import turip.place.repository.PlaceCategoryRepository;
import turip.place.repository.PlaceRepository;
import turip.region.domain.City;
import turip.region.domain.Country;
import turip.region.domain.Province;
import turip.region.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class AdminContentServiceTest {

    @InjectMocks
    private AdminContentService adminContentService;

    @Mock
    private ContentRepository contentRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private ContentPlaceRepository contentPlaceRepository;
    @Mock
    private CreatorRepository creatorRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PlaceCategoryRepository placeCategoryRepository;

    private AdminContentSaveRequest request;
    private Creator creator;
    private City city;
    private Place place;
    private Category category;

    @BeforeEach
    void setUp() {
        AdminContentSaveRequest.VideoRequest videoRequest = new AdminContentSaveRequest.VideoRequest(
                "videoId123",
                "Test Title",
                "http://test.url",
                "Test Channel",
                "http://channel.image",
                LocalDate.now()
        );
        AdminContentSaveRequest.PlaceRequest placeRequest = new AdminContentSaveRequest.PlaceRequest(
                "Test Place",
                "http://place.url",
                "Test Address",
                37.5665,
                126.9780,
                "Restaurant"
        );
        AdminContentSaveRequest.ContentPlaceRequest contentPlaceRequest = new AdminContentSaveRequest.ContentPlaceRequest(
                1,
                1,
                "00:30",
                placeRequest
        );
        AdminContentSaveRequest.TripDurationRequest tripDurationRequest = new AdminContentSaveRequest.TripDurationRequest(2, 3);
        request = new AdminContentSaveRequest(
                "Seoul",
                videoRequest,
                tripDurationRequest,
                Collections.singletonList(contentPlaceRequest)
        );

        creator = new Creator("Test Channel", "http://channel.image");
        Country country = new Country("South Korea", "http://country.image");
        Province province = new Province("Seoul");
        city = new City(country, province, "Seoul", "http://seoul.image");
        place = new Place("Test Place", "http://place.url", "Test Address", 37.5665, 126.9780);
        category = new Category("Restaurant");
    }

    @Test
    @DisplayName("새로운 콘텐츠를 저장한다.")
    void saveNewContent() {
        // given
        when(creatorRepository.findByChannelName("Test Channel")).thenReturn(Optional.empty());
        when(creatorRepository.save(any(Creator.class))).thenReturn(creator);
        when(cityRepository.findByName("Seoul")).thenReturn(Optional.of(city));
        when(placeRepository.findByUrl("http://place.url")).thenReturn(Optional.empty());
        when(placeRepository.save(any(Place.class))).thenReturn(place);
        when(categoryRepository.findByName("Restaurant")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(placeCategoryRepository.findByPlaceAndCategory(place, category)).thenReturn(Optional.empty());

        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content contentToSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(contentToSave, "id", 1L);
            return contentToSave;
        });

        // when
        Long contentId = adminContentService.save(request);

        // then
        assertThat(contentId).isEqualTo(1L);
        verify(placeCategoryRepository).save(any(PlaceCategory.class));
    }

    @Test
    @DisplayName("타임라인 형식이 null이면 예외를 던진다.")
    void parseTimeLine_withNull_throwsException() {
        // given
        AdminContentSaveRequest.ContentPlaceRequest badContentPlaceRequest = new AdminContentSaveRequest.ContentPlaceRequest(
                1, 1, null, request.contentPlaces().get(0).place());
        AdminContentSaveRequest newRequest = new AdminContentSaveRequest(
                request.cityName(),
                request.video(),
                request.tripDuration(),
                Collections.singletonList(badContentPlaceRequest)
        );

        when(creatorRepository.findByChannelName(any())).thenReturn(Optional.of(creator));
        when(cityRepository.findByName(any())).thenReturn(Optional.of(city));
        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content contentToSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(contentToSave, "id", 1L);
            return contentToSave;
        });

        // when & then
        assertThatThrownBy(() -> adminContentService.save(newRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorTag.INVALID_TIMELINE_FORMAT.getMessage());
    }

    @Test
    @DisplayName("타임라인 형식이 mm:ss가 아니면 예외를 던진다.")
    void parseTimeLine_withInvalidFormat_throwsException() {
        // given
        AdminContentSaveRequest.ContentPlaceRequest badContentPlaceRequest = new AdminContentSaveRequest.ContentPlaceRequest(
                1, 1, "12345", request.contentPlaces().get(0).place());
        AdminContentSaveRequest newRequest = new AdminContentSaveRequest(
                request.cityName(),
                request.video(),
                request.tripDuration(),
                Collections.singletonList(badContentPlaceRequest)
        );

        when(creatorRepository.findByChannelName(any())).thenReturn(Optional.of(creator));
        when(cityRepository.findByName(any())).thenReturn(Optional.of(city));
        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content contentToSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(contentToSave, "id", 1L);
            return contentToSave;
        });

        // when & then
        assertThatThrownBy(() -> adminContentService.save(newRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorTag.INVALID_TIMELINE_FORMAT.getMessage());
    }

    @Test
    @DisplayName("타임라인이 60분 이상이면 예외를 던진다.")
    void parseTimeLine_withMinutesOverflow_throwsException() {
        // given
        AdminContentSaveRequest.ContentPlaceRequest badContentPlaceRequest = new AdminContentSaveRequest.ContentPlaceRequest(
                1, 1, "60:00", request.contentPlaces().get(0).place());
        AdminContentSaveRequest newRequest = new AdminContentSaveRequest(
                request.cityName(),
                request.video(),
                request.tripDuration(),
                Collections.singletonList(badContentPlaceRequest)
        );

        when(creatorRepository.findByChannelName(any())).thenReturn(Optional.of(creator));
        when(cityRepository.findByName(any())).thenReturn(Optional.of(city));
        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content contentToSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(contentToSave, "id", 1L);
            return contentToSave;
        });

        // when & then
        assertThatThrownBy(() -> adminContentService.save(newRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorTag.INVALID_TIMELINE_FORMAT.getMessage());
    }
}