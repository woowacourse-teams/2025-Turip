package com.on.turip.feature.article.impl

import com.on.turip.core.ui.UiEffect

sealed interface ArticleListEffect : UiEffect {
    data object NavigateToLogin : ArticleListEffect
}
