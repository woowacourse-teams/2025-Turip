package com.on.turip.data.searchhistory

import com.on.turip.domain.searchhistory.SearchHistory

fun SearchHistoryEntity.toDomain(): SearchHistory =
    SearchHistory(
        this.keyword,
        this.history,
    )
