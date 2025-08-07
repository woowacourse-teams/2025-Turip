package com.on.turip.data.searchhistory

import com.on.turip.domain.searchhistory.SearchHistory

fun SearchHistoryEntity.toDomain(): SearchHistory =
    SearchHistory(
        keyword = this.keyword,
        historyTime = this.history,
    )
