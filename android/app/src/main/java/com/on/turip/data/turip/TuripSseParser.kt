package com.on.turip.data.turip

import com.on.turip.data.turip.stream.TuripStreamConnectPayload
import com.on.turip.data.turip.stream.TuripStreamFolderUpdatePayload
import com.on.turip.data.turip.stream.TuripStreamHeartbeatPayload
import com.on.turip.data.turip.stream.TuripStreamMemberUpdatePayload
import com.on.turip.domain.turip.TuripSseEventType
import com.on.turip.domain.turip.TuripStreamEvent
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class TuripSseParser @Inject constructor(
    private val json: Json,
) {
    fun parse(
        eventId: String?,
        eventType: String,
        data: String,
    ): TuripStreamEvent? =
        runCatching {
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
        }.getOrElse { throwable ->
            Timber.e(
                throwable,
                "튜립 SSE payload 파싱 실패. id=%s, type=%s, dataLen=%s",
                eventId,
                eventType,
                data.length,
            )
            null
        }
}
