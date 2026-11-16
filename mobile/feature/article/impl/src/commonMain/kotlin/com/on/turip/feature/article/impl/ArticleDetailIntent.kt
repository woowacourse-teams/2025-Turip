package com.on.turip.feature.article.impl

import com.on.turip.core.ui.UiIntent
import com.on.turip.feature.article.impl.model.ArticlePlaceUiModel

sealed interface ArticleDetailIntent : UiIntent {
    data object Retry : ArticleDetailIntent

    data class ClickPlaceMap(
        val place: ArticlePlaceUiModel,
    ) : ArticleDetailIntent
}
