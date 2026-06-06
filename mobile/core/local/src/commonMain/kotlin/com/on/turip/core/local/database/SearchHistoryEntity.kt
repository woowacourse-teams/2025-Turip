package com.on.turip.core.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.on.turip.core.common.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "search_history")
data class SearchHistoryEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey
    val keyword: String,
    @ColumnInfo(name = "history")
    val history: Long = Clock.System.now().toEpochMilliseconds()
)
