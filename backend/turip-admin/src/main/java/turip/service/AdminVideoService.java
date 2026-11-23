package turip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.content.repository.ContentRepository;
import turip.controller.dto.response.AdminVideoResponse;
import turip.creator.repository.CreatorRepository;
import turip.infrastructure.client.YoutubeVideoSearchClient;
import turip.infrastructure.client.dto.YoutubeVideoSearchResponse;
import turip.util.YoutubeVideoUrlParser;

@Service
@RequiredArgsConstructor
public class AdminVideoService {

    private final YoutubeVideoSearchClient youtubeVideoSearchClient;
    private final CreatorRepository creatorRepository;
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public AdminVideoResponse getVideoData(String videoUrl) {
        String videoId = YoutubeVideoUrlParser.extractVideoId(videoUrl);
        if (videoId == null) {
            throw new BadRequestException(ErrorTag.INVALID_YOUTUBE_URL);
        }

        YoutubeVideoSearchResponse searchResponse = youtubeVideoSearchClient.searchByVideoId(videoId);

        boolean isExisting = creatorRepository.findByChannelName(searchResponse.channelName())
                .flatMap(creator -> contentRepository.findByCreatorAndTitle(creator, searchResponse.title()))
                .isPresent();

        return AdminVideoResponse.of(
                searchResponse.videoId(),
                searchResponse.title(),
                searchResponse.channelId(),
                searchResponse.channelName(),
                searchResponse.uploadedDate(),
                isExisting
        );
    }
}
