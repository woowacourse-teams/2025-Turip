package turip.creator.controller.dto.response;

import turip.creator.domain.Creator;

public record CreatorResponse(
        Long id,
        String channelName,
        String profileImage
) {

    public static CreatorResponse from(Creator creator) {
        return new CreatorResponse(
                creator.getId(),
                creator.getChannelName(),
                creator.getChannelName()
        );
    }
}
