package turip.util.fixture;

import java.time.LocalDate;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.content.domain.Content;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.domain.ContentPendingData.ContentPlaceData;
import turip.content.domain.ContentPendingData.PlaceData;
import turip.content.domain.ContentPendingData.VideoData;
import turip.content.domain.ContentPendingStatus;

public class ContentPendingFixture {

    public static ContentPendingData createTestContentData(String cityName) {
        VideoData video = new VideoData(
                "video123",
                "테스트 비디오",
                "https://youtube.com/watch?v=test",
                "테스트 채널",
                "https://example.com/channel.jpg",
                LocalDate.of(2024, 1, 1)
        );

        PlaceData place = new PlaceData(
                "테스트 장소",
                "https://example.com/place",
                "서울시 강남구",
                37.5,
                127.0,
                "카페"
        );

        ContentPlaceData contentPlace = new ContentPlaceData(
                1,
                1,
                "10:00",
                place
        );

        return new ContentPendingData(cityName, video, List.of(contentPlace));
    }

    public static ContentPending createPending(Account collector) {
        ContentPendingData contentData = createTestContentData("서울");
        return new ContentPending(
                contentData,
                collector,
                null,
                null,
                null
        );
    }

    public static ContentPending createApproved(Account collector, Account validator, Content content) {
        ContentPendingData contentData = createTestContentData("서울");
        ContentPending pending = new ContentPending(
                contentData,
                collector,
                null,
                null,
                null
        );
        ReflectionTestUtils.setField(pending, "status", ContentPendingStatus.APPROVED);
        ReflectionTestUtils.setField(pending, "validatorAccount", validator);
        ReflectionTestUtils.setField(pending, "content", content);
        return pending;
    }

    public static ContentPending createPendingWithData(ContentPendingData contentData, Account collector) {
        return new ContentPending(
                contentData,
                collector,
                null,
                null,
                null
        );
    }
}
