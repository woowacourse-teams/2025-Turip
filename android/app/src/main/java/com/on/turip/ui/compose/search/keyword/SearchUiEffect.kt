package com.on.turip.ui.compose.search.keyword

sealed interface SearchUiEffect {
    data object NavigateToLogin : SearchUiEffect
}
