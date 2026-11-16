package com.on.turip.feature.article.impl

import com.on.turip.core.ui.UiEffect

sealed interface ArticleDetailEffect : UiEffect {
    data object NavigateToLogin : ArticleDetailEffect

    data class OpenMap(
        val url: String,
    ) : ArticleDetailEffect
}
