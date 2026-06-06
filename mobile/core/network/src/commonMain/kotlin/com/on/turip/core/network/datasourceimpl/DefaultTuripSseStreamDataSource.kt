package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.domain.turip.TuripStreamService
import com.on.turip.core.model.turip.TuripStreamEvent
import com.on.turip.core.data.datasource.TuripSseStreamDataSource
import com.on.turip.core.network.sse.TuripSseParser
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultTuripSseStreamDataSource(
    private val turipStreamService: TuripStreamService,
    private val parser: TuripSseParser,
) : TuripSseStreamDataSource {
    override fun streamTuripEvents(turipId: Long): Flow<TuripStreamEvent> =
        flow {
            turipStreamService
                .streamTuripEvents(turipId)
                .collect { event ->
                    val eventType: String =
                        event.event ?: run {
                            Napier.e("튜립 SSE 이벤트 타입 누락. id=%s", event.id)
                            return@collect
                        }
                    val data: String =
                        event.data ?: run {
                            Napier.e("튜립 SSE 이벤트 데이터 누락. id=%s", event.id)
                            return@collect
                        }

                    val parsed: TuripStreamEvent =
                        parser.parse(event.id, eventType, data) ?: run {
                            Napier.e(
                                "튜립 SSE 이벤트 파싱 실패. id=%s, type=%s",
                                event.id,
                                eventType,
                            )
                            return@collect
                        }

                    emit(parsed)
                }
        }
}
