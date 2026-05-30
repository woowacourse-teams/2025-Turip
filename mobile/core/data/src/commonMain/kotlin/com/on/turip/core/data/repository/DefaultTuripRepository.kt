package com.on.turip.core.data.repository

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.network.datasource.TuripDatasource

class DefaultTuripRepository(
    private val turipDatasource: TuripDatasource,
) : TuripRepository {
    override suspend fun getTurips(): Result<List<Turip>> =
        safeApiCall { turipDatasource.getTurips().map { it.toDomain() } }

    override suspend fun getTurip(turipId: Long): Result<Turip> =
        safeApiCall { turipDatasource.getTurip(turipId).toDomain() }

    override suspend fun getTuripMembers(turipId: Long): Result<List<Member>> =
        safeApiCall { turipDatasource.getTuripMembers(turipId).map { it.toDomain() } }

    override suspend fun createTurip(name: String): Result<Turip> =
        safeApiCall { turipDatasource.createTurip(name).toDomain() }

    override suspend fun renameTurip(turipId: Long, name: String): Result<Unit> =
        safeApiCall { turipDatasource.renameTurip(turipId, name) }

    override suspend fun deleteTurip(turipId: Long): Result<Unit> =
        safeApiCall { turipDatasource.deleteTurip(turipId) }

    override suspend fun exitTurip(turipId: Long): Result<Unit> =
        safeApiCall { turipDatasource.exitTurip(turipId) }

    override suspend fun getTuripsByPlaceId(placeId: Long): Result<List<TuripStatus>> =
        safeApiCall { turipDatasource.getTuripsByPlaceId(placeId).map { it.toDomain() } }

    override suspend fun getTuripPlaces(turipId: Long): Result<List<TuripPlace>> =
        safeApiCall { turipDatasource.getTuripPlaces(turipId).map { it.toDomain() } }

    override suspend fun addTuripPlace(turipId: Long, placeId: Long): Result<Unit> =
        safeApiCall { turipDatasource.addTuripPlace(turipId, placeId) }

    override suspend fun removeTuripPlace(turipId: Long, placeId: Long): Result<Unit> =
        safeApiCall { turipDatasource.removeTuripPlace(turipId, placeId) }

    override suspend fun reorderTuripPlaces(turipId: Long, placeIds: List<Long>): Result<Unit> =
        safeApiCall { turipDatasource.reorderTuripPlaces(turipId, placeIds) }

    override suspend fun updatePlaceTurips(placeId: Long, turipIds: List<Long>): Result<Unit> =
        safeApiCall { turipDatasource.updatePlaceTurips(placeId, turipIds) }

    override suspend fun generateInvitationToken(turipId: Long): Result<String> =
        safeApiCall { turipDatasource.generateInvitationToken(turipId).token }

    override suspend fun getInvitationInformation(token: String): Result<TuripInvitationInformation> =
        safeApiCall { turipDatasource.getInvitationInformation(token).toDomain() }

    override suspend fun joinTurip(turipId: Long): Result<Unit> =
        safeApiCall { turipDatasource.joinTurip(turipId) }
}