package com.on.turip.feature.search.impl.keyword

sealed interface SearchUiEffect {
    data object NavigateToLogin : SearchUiEffect
}
