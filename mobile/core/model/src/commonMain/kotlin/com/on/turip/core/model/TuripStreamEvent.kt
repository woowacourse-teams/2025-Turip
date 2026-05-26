package com.on.turip.core.model

sealed interface TuripStreamEvent {
    data class Connect(
        val turipId: Long,
        val timestamp: Long,
    ) : TuripStreamEvent

    data class FolderUpdate(
        val turipId: Long,
        val action: FolderUpdateAction,
        val timestamp: Long,
    ) : TuripStreamEvent

    data class MemberUpdate(
        val turipId: Long,
        val action: MemberUpdateAction,
        val memberCount: Int,
        val members: List<Member>,
    ) : TuripStreamEvent

    data class Heartbeat(val timestamp: Long) : TuripStreamEvent
}

enum class FolderUpdateAction {
    PLACE_REORDERED,
    PLACE_ADDED,
    PLACE_DELETED,
    FOLDER_NAME_CHANGED,
    UNKNOWN,
}

enum class MemberUpdateAction {
    MEMBER_JOINED,
    MEMBER_EXITED,
    UNKNOWN,
}
