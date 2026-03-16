package com.on.turip.domain.turip

import com.on.turip.data.turip.stream.TuripStreamConnectPayload
import com.on.turip.data.turip.stream.TuripStreamFolderUpdatePayload
import com.on.turip.data.turip.stream.TuripStreamHeartbeatPayload
import com.on.turip.data.turip.stream.TuripStreamMemberUpdatePayload
import com.on.turip.data.turip.toDomain
import kotlinx.serialization.json.Json
import timber.log.Timber

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
            TuripSseEventType.CONNECT -> {
                json
                    .decodeFromString<TuripStreamConnectPayload>(data)
                    .toDomain(eventId ?: "")
            }

            TuripSseEventType.FOLDER_UPDATE -> {
                json
                    .decodeFromString<TuripStreamFolderUpdatePayload>(data)
                    .toDomain(eventId ?: "")
            }

            TuripSseEventType.MEMBER_UPDATE -> {
                json
                    .decodeFromString<TuripStreamMemberUpdatePayload>(data)
                    .toDomain(eventId ?: "")
            }

            TuripSseEventType.HEARTBEAT -> {
                json
                    .decodeFromString<TuripStreamHeartbeatPayload>(data)
                    .toDomain(eventId ?: "")
            }

            else -> {
                Timber.e(
                    "튜립 SSE 미지원 타입. id=%s, type=%s, dataLen=%s",
                    eventId,
                    eventType,
                    data.length,
                )
                null
            }
        }
}
