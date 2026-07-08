package turip.account.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    NEW_MEMBER_JOINED(
            "새로운 멤버를 환영해주세요!",
            "'%s'에 새로운 멤버가 참여했습니다."
    ),
    ;

    private final String title;
    private final String messageTemplate;

    public FcmAlertMessage createMessage(Object... args) {
        String formattedMessage = String.format(messageTemplate, args);
        return FcmAlertMessage.of(title, formattedMessage);
    }
}
