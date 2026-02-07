package turip.favorite.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StreamEventType {

    CONNECT("connect"),
    FOLDER_UPDATE("folder-update"),
    MEMBER_UPDATE("member-update"),
    HEARTBEAT("heartbeat"),
    ;

    private final String name;
}
