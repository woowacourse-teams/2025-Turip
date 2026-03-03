package turip.util.fixture;

import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;
import turip.place.domain.Place;

public class PlaceFixture {

    public static Place create() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return new Place(
                "장소명" + uniqueId,
                "https://place.example.com/" + uniqueId,
                "주소" + uniqueId,
                37.5665,
                126.9780
        );
    }

    public static Place createWithId(Long id) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Place place = new Place(
                "장소명" + uniqueId,
                "https://place.example.com/" + uniqueId,
                "주소" + uniqueId,
                37.5665,
                126.9780
        );
        ReflectionTestUtils.setField(place, "id", id);
        return place;
    }
}
