package com.on.turip.core.network.datasource

import com.on.turip.core.domain.turip.TuripStreamService
import com.on.turip.core.model.FolderUpdateAction
import com.on.turip.core.model.MemberUpdateAction
import com.on.turip.core.model.TuripStreamEvent
import com.on.turip.core.network.ApiPath
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class DefaultTuripStreamService(
    private val sseHttpClient: HttpClient,
) : TuripStreamService {
    private val json = Json { ignoreUnknownKeys = true }

    override fun observeTuripStream(turipId: Long): Flow<TuripStreamEvent> = flow {
        sseHttpClient.sse(urlString = "${ApiPath.V1}turips/$turipId/stream") {
            incoming.collect { event ->
                val data = event.data ?: return@collect
                runCatching {
                    val element = json.parseToJsonElement(data).jsonObject
                    val type = element["type"]?.jsonPrimitive?.content ?: return@collect
                    val parsedEvent = parseEvent(type, element, turipId) ?: return@collect
                    emit(parsedEvent)
                }
            }
        }
    }

    private fun parseEvent(
        type: String,
        element: JsonObject,
        turipId: Long,
    ): TuripStreamEvent? = when (type) {
        "CONNECT" -> TuripStreamEvent.Connect(
            turipId = turipId,
            timestamp = element["timestamp"]?.jsonPrimitive?.long ?: 0L,
        )
        "FOLDER_UPDATE" -> TuripStreamEvent.FolderUpdate(
            turipId = turipId,
            action = element["action"]?.jsonPrimitive?.content
                ?.let { parseFolderAction(it) } ?: FolderUpdateAction.UNKNOWN,
            timestamp = element["timestamp"]?.jsonPrimitive?.long ?: 0L,
        )
        "MEMBER_UPDATE" -> TuripStreamEvent.MemberUpdate(
            turipId = turipId,
            action = element["action"]?.jsonPrimitive?.content
                ?.let { parseMemberAction(it) } ?: MemberUpdateAction.UNKNOWN,
            memberCount = element["memberCount"]?.jsonPrimitive?.int ?: 0,
            members = emptyList(),
        )
        "HEARTBEAT" -> TuripStreamEvent.Heartbeat(
            timestamp = element["timestamp"]?.jsonPrimitive?.long ?: 0L,
        )
        else -> null
    }

    private fun parseFolderAction(action: String): FolderUpdateAction = when (action) {
        "PLACE_REORDERED" -> FolderUpdateAction.PLACE_REORDERED
        "PLACE_ADDED" -> FolderUpdateAction.PLACE_ADDED
        "PLACE_DELETED" -> FolderUpdateAction.PLACE_DELETED
        "FOLDER_NAME_CHANGED" -> FolderUpdateAction.FOLDER_NAME_CHANGED
        else -> FolderUpdateAction.UNKNOWN
    }

    private fun parseMemberAction(action: String): MemberUpdateAction = when (action) {
        "MEMBER_JOINED" -> MemberUpdateAction.MEMBER_JOINED
        "MEMBER_EXITED" -> MemberUpdateAction.MEMBER_EXITED
        else -> MemberUpdateAction.UNKNOWN
    }
}
