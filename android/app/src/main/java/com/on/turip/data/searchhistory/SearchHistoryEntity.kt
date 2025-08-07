package com.on.turip.data.searchhistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val keyword: String,
    @ColumnInfo(name = "history")
    val history: Long = System.currentTimeMillis(),
)
