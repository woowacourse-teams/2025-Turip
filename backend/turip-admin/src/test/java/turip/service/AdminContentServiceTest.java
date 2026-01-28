package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import turip.content.domain.Content;
import turip.content.domain.ContentPlace;
import turip.content.repository.ContentPlaceRepository;
import turip.content.repository.ContentRepository;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.place.domain.Place;
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
    private CreatorRepository creatorRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ContentPlaceRepository contentPlaceRepository;

    private AdminContentSaveRequest request;
    private Creator creator;
    private City city;
    private Place place;

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
        AdminContentSaveRequest.TripDurationRequest tripDurationRequest = new AdminContentSaveRequest.TripDurationRequest(
                2, 3);
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
    }

    @Test
    @DisplayName("콘텐츠를 저장한다.")
    void saveContent() {
        // given
        when(creatorRepository.findByChannelName("Test Channel")).thenReturn(Optional.empty());
        when(creatorRepository.save(any(Creator.class))).thenReturn(creator);
        when(cityRepository.findByName("Seoul")).thenReturn(Optional.of(city));
        when(placeRepository.findByUrl("http://place.url")).thenReturn(Optional.empty());
        when(placeRepository.save(any(Place.class))).thenReturn(place);
        when(contentPlaceRepository.save(any(ContentPlace.class))).thenAnswer(i -> i.getArgument(0));
        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content savedContent = invocation.getArgument(0);
            return new Content(1L, savedContent.getCreator(), savedContent.getCity(), savedContent.getTitle(),
                    savedContent.getUrl(), savedContent.getUploadedDate());
        });

        // when
        Long contentId = adminContentService.save(request);

        // then
        assertThat(contentId).isEqualTo(1L);
    }
}
