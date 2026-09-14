package com.on.turip.feature.article.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.article.api.ArticleDetailNavKey
import com.on.turip.feature.article.impl.ArticleDetailScreen
import com.on.turip.feature.article.impl.platform.rememberArticleDetailPlatformActions
import com.on.turip.feature.login.api.LoginNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class ArticleNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(ArticleDetailNavKey::class, ArticleDetailNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<ArticleDetailNavKey> { key ->
            val platformActions = rememberArticleDetailPlatformActions()
            ArticleDetailScreen(
                articleId = key.articleId,
                onBackClick = navigator::goBack,
                onNavigateToLoginScreen = { navigator.goWithAllClear(LoginNavKey()) },
                onOpenUrl = platformActions.openUrl,
            )
        }
    }
}
