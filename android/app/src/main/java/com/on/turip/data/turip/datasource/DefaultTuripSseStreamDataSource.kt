package com.on.turip.data.turip.datasource

import com.on.turip.data.turip.TuripSseParser
import com.on.turip.data.turip.service.TuripStreamService
import com.on.turip.domain.turip.TuripStreamEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class DefaultTuripSseStreamDataSource @Inject constructor(
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
                            Timber.e("튜립 SSE 이벤트 타입 누락. id=%s", event.id)
                            return@collect
                        }
                    val data: String =
                        event.data ?: run {
                            Timber.e("튜립 SSE 이벤트 데이터 누락. id=%s", event.id)
                            return@collect
                        }

                    val parsed: TuripStreamEvent =
                        parser.parse(event.id, eventType, data) ?: run {
                            Timber.e(
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
