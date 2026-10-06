package com.on.turip.feature.article.impl.platform

import androidx.compose.runtime.Composable

internal class ArticleDetailPlatformActions(
    val openUrl: (String) -> Unit,
)

@Composable
internal expect fun rememberArticleDetailPlatformActions(): ArticleDetailPlatformActions
