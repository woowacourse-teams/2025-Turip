package com.on.turip.feature.article.impl

import com.on.turip.core.ui.UiIntent

sealed interface ArticleListIntent : UiIntent {
    data object Retry : ArticleListIntent

    data object LoadNextPage : ArticleListIntent
}
