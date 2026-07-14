package com.on.turip.core.data.repository

import com.on.turip.core.common.toErrorType
import com.on.turip.core.data.datasource.TuripRemoteDataSource
import com.on.turip.core.data.datasource.TuripSseStreamDataSource
import com.on.turip.core.data.dto.turip.PlaceTuripsRequest
import com.on.turip.core.data.dto.turip.TuripPlaceOrderRequest
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.data.mapper.toPatchRequestDto
import com.on.turip.core.data.mapper.toPostRequestDto
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.model.bookmark.TuripPlace
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripInvitationInformation
import com.on.turip.core.model.turip.TuripInvitationToken
import com.on.turip.core.model.turip.TuripMember
import com.on.turip.core.model.turip.TuripStreamEvent
import com.on.turip.core.model.turip.result.TuripStreamResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DefaultTuripRepository(
    private val turipRestRemoteDataSource: TuripRemoteDataSource,
    private val turipSseStreamDataSource: TuripSseStreamDataSource,
) : TuripRepository {
    private val _turips = MutableStateFlow<List<Turip>>(emptyList())
    override val turips: StateFlow<List<Turip>> = _turips.asStateFlow()
    private var isUpdatingPlaceTurips = false

    override suspend fun loadTurip(turipId: Long): TuripResult<Turip> =
        turipRestRemoteDataSource.getTurip(turipId).mapCatching { it.toDomain() }.also { result ->
            result.onSuccess { turip ->
                _turips.update { current -> current.map { if (it.id == turip.id) turip else it } }
            }
        }

    override suspend fun loadTurips(): TuripResult<List<Turip>> =
        turipRestRemoteDataSource.getTurips().mapCatching { it.toDomain() }.also { result ->
            result.onSuccess { turips -> _turips.value = turips }
        }

    override suspend fun loadTuripMembers(turipId: Long): TuripResult<List<TuripMember>> =
        turipRestRemoteDataSource.getTuripMembers(turipId).mapCatching { it.toDomain() }

    override suspend fun createTurip(name: String): TuripResult<Turip> =
        turipRestRemoteDataSource
            .postTurip(name.toPostRequestDto())
            .mapCatching { it.toDomain() }
            .also { result ->
                result.onSuccess { turip -> _turips.update { current -> current + turip } }
            }

    override suspend fun updateTurip(
        turipId: Long,
        updateName: String,
    ): TuripResult<Unit> =
        turipRestRemoteDataSource
            .patchTurip(turipId, updateName.toPatchRequestDto())
            .also { result ->
                result.onSuccess {
                    _turips.update { current -> current.map { if (it.id == turipId) it.copy(name = updateName) else it } }
                }
            }

    override suspend fun deleteTurip(turipId: Long): TuripResult<Unit> =
        turipRestRemoteDataSource.deleteTurip(turipId).also { result ->
            result.onSuccess { _turips.update { current -> current.filter { it.id != turipId } } }
        }

    override suspend fun exitTurip(turipId: Long): TuripResult<Unit> =
        turipRestRemoteDataSource.exitTurip(turipId).also { result ->
            result.onSuccess { _turips.update { current -> current.filter { it.id != turipId } } }
        }

    override suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>> =
        turipRestRemoteDataSource
            .getTuripsByPlaceId(placeId)
            .mapCatching { it.toDomain() }

    override suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>> =
        turipRestRemoteDataSource.getTuripPlaces(turipId).mapCatching { it.toDomain() }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> =
        turipRestRemoteDataSource
            .createTuripPlace(turipId = turipId, placeId = placeId)
            .also { result ->
                result.onSuccess {
                    _turips.update { current ->
                        current.map { if (it.id == turipId) it.copy(placeCount = it.placeCount + 1) else it }
                    }
                }
            }

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> =
        turipRestRemoteDataSource
            .deleteTuripPlace(turipId = turipId, placeId = placeId)
            .also { result ->
                result.onSuccess {
                    _turips.update { current ->
                        current.map {
                            if (it.id == turipId) {
                                it.copy(placeCount = (it.placeCount - 1).coerceAtLeast(0))
                            } else {
                                it
                            }
                        }
                    }
                }
            }

    override suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit> =
        turipRestRemoteDataSource
            .patchTuripPlacesOrder(
                turipId = turipId,
                turipPlaceOrderRequest = TuripPlaceOrderRequest(turipPlaceIdsOrder = updatedOrder),
            )

    override suspend fun updatePlaceTurips(
        placeId: Long,
        turipIds: List<Long>,
        previouslySelectedIds: Set<Long>,
    ): TuripResult<Unit> {
        if (isUpdatingPlaceTurips) return TuripResult.Success(Unit)
        isUpdatingPlaceTurips = true

        try {
            val turipIdsSet = turipIds.toSet()
            return turipRestRemoteDataSource
                .putPlaceTurips(
                    placeId = placeId,
                    placeTuripsRequest = PlaceTuripsRequest(turipIds),
                ).also { result ->
                    result.onSuccess {
                        _turips.update { current ->
                            current.map { turip ->
                                val wasSelected = turip.id in previouslySelectedIds
                                val isNowSelected = turip.id in turipIdsSet
                                when {
                                    !wasSelected && isNowSelected -> {
                                        turip.copy(placeCount = turip.placeCount + 1)
                                    }

                                    wasSelected && !isNowSelected -> {
                                        turip.copy(placeCount = (turip.placeCount - 1).coerceAtLeast(0))
                                    }

                                    else -> {
                                        turip
                                    }
                                }
                            }
                        }
                    }
                }
        } finally {
            isUpdatingPlaceTurips = false
        }
    }

    override suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationToken> =
        turipRestRemoteDataSource.createInvitationToken(turipId).mapCatching { it.toDomain() }

    override suspend fun joinTurip(turipId: Long): TuripResult<Unit> = turipRestRemoteDataSource.joinTurip(turipId).mapCatching { Unit }

    override suspend fun verifyInvitationToken(token: String): TuripResult<TuripInvitationInformation> =
        turipRestRemoteDataSource.getInvitationInformation(token).mapCatching { it.toDomain() }

    override fun clearCache() {
        _turips.value = emptyList()
    }

    override fun streamTuripEvents(turipId: Long): Flow<TuripStreamResult> =
        turipSseStreamDataSource
            .streamTuripEvents(turipId)
            .map<TuripStreamEvent, TuripStreamResult> { event ->
                TuripStreamResult.Event(event)
            }.catch { throwable ->
                val fatalResult =
                    when (val errorType = throwable.toErrorType()) {
                        ErrorType.Auth.TokenExpired -> TuripStreamResult.Fatal.TokenExpired
                        ErrorType.Auth.Forbidden -> TuripStreamResult.Fatal.Forbidden
                        else -> TuripStreamResult.Fatal.ConnectionLost(errorType)
                    }

                emit(fatalResult)
            }

    override fun updateCachedTuripMemberCount(
        turipId: Long,
        memberCount: Int,
    ) {
        _turips.update { current ->
            current.map { turip ->
                if (turip.id == turipId) {
                    turip.copy(memberCount = memberCount.coerceAtLeast(0))
                } else {
                    turip
                }
            }
        }
    }

    override fun updateCachedTuripSharedStatus(
        turipId: Long,
        isShared: Boolean,
    ) {
        _turips.update { current ->
            current.map { turip ->
                if (turip.id == turipId) {
                    turip.copy(isShared = isShared)
                } else {
                    turip
                }
            }
        }
    }
}
