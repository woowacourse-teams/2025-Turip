package com.on.turip.feature.search.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface SearchIntent : UiIntent {
    data class UpdateKeyword(val keyword: String) : SearchIntent
    data object Search : SearchIntent
    data class ClickHistory(val keyword: String) : SearchIntent
    data class DeleteHistory(val keyword: String) : SearchIntent
    data object ShowHistory : SearchIntent
    data object HideHistory : SearchIntent
    data object Retry : SearchIntent
}
