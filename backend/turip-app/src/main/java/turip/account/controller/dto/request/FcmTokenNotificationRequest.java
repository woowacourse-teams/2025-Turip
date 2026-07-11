package turip.account.controller.dto.request;

import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;

public record FcmTokenNotificationRequest(Boolean notificationEnabled) {

    public FcmTokenNotificationRequest {
        if (notificationEnabled == null) {
            throw new BadRequestException(ErrorTag.NOTIFICATION_ENABLED_REQUIRED);
        }
    }
}
