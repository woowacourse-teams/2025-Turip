package com.on.turip.ui.search.keywordresult

sealed interface SearchUiEffect {
    data object NavigateToLogin : SearchUiEffect
}
