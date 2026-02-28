package com.on.turip.ui.compose.turip.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DeleteTuripSnapShot(
    val deleteTurip: MyTuripModel,
    val originTurips: ImmutableList<MyTuripModel>,
) {
    val hasSnapshot: Boolean get() = this != EMPTY

    companion object {
        val EMPTY =
            DeleteTuripSnapShot(
                deleteTurip = MyTuripModel.Idle,
                originTurips = persistentListOf(),
            )
    }
}
