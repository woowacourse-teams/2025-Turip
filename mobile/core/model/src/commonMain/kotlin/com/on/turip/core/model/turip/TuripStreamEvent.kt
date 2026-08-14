package com.on.turip.core.model.turip

sealed interface TuripStreamEvent {
    val id: String

    data class Connect(
        override val id: String,
        val turipId: Long,
        val timestamp: String,
    ) : TuripStreamEvent

    data class FolderUpdate(
        override val id: String,
        val turipId: Long,
        val action: FolderAction,
        val timestamp: String,
    ) : TuripStreamEvent

    data class MemberUpdate(
        override val id: String,
        val turipId: Long,
        val action: MemberAction,
        val memberCount: Int,
        val members: List<Member>,
        val timestamp: String,
    ) : TuripStreamEvent

    data class Heartbeat(
        override val id: String,
        val timestamp: String,
    ) : TuripStreamEvent

    enum class FolderAction {
        PLACE_REORDERED,
        PLACE_ADDED,
        PLACE_DELETED,
        FOLDER_NAME_CHANGED,
        FOLDER_DELETED,
        FOLDER_PLACE_CHANGED,
        UNKNOWN,
    }

    enum class MemberAction {
        MEMBER_JOINED,
        MEMBER_EXITED,
        UNKNOWN,
    }

    data class Member(
        val nickname: String,
    )
}
