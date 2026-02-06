package turip.favorite.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventType {

    CONNECT("connect"),
    FOLDER_UPDATE("folder-update"),
    MEMBER_UPDATE("member-update"),
    ;

    private final String name;
}
