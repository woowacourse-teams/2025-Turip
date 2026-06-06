package com.on.turip.core.network.sse

import com.on.turip.core.model.turip.TuripStreamEvent
import com.on.turip.core.network.dto.sse.TuripStreamConnectPayload
import com.on.turip.data.turip.stream.TuripStreamFolderUpdatePayload
import com.on.turip.data.turip.stream.TuripStreamHeartbeatPayload
import com.on.turip.data.turip.stream.TuripStreamMemberUpdatePayload
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

class TuripSseParser(
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
                    Napier.e(
                        "튜립 SSE 미지원 타입. id=%s, type=%s, dataLen=%s",
                    )
                    null
                }
            }
        }.getOrElse { throwable ->
            Napier.e(
                "튜립 SSE payload 파싱 실패. id=%s, type=%s, dataLen=%s",
            )
            null
        }
}
