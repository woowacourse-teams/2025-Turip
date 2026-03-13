package com.on.turip.domain.turip

import com.on.turip.data.turip.stream.TuripStreamConnectPayload
import com.on.turip.data.turip.stream.TuripStreamFolderUpdatePayload
import com.on.turip.data.turip.stream.TuripStreamHeartbeatPayload
import com.on.turip.data.turip.stream.TuripStreamMemberUpdatePayload
import com.on.turip.data.turip.toDomain
import kotlinx.serialization.json.Json

class TuripSseParser(
    private val json: Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        },
) {
    fun parse(
        eventId: String?,
        eventType: String,
        data: String,
    ): TuripStreamEvent? =
        when (eventType) {
            "connect" -> {
                json
                    .decodeFromString<TuripStreamConnectPayload>(data)
                    .toDomain(eventId ?: "")
            }

            "folder-update" -> {
                json
                    .decodeFromString<TuripStreamFolderUpdatePayload>(data)
                    .toDomain(eventId ?: "")
            }

            "member-update" -> {
                json
                    .decodeFromString<TuripStreamMemberUpdatePayload>(data)
                    .toDomain(eventId ?: "")
            }

            "heartbeat", "heart-beat" -> {
                json
                    .decodeFromString<TuripStreamHeartbeatPayload>(data)
                    .toDomain(eventId ?: "")
            }

            else -> {
                null
            }
        }
}
