package com.on.turip.feature.article.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.article.impl.ArticleDetailViewModel
import com.on.turip.feature.article.impl.navigation.ArticleNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val articleModule = module {
    single { ArticleNavKeyProvider() } bind NavKeyProvider::class
    viewModel<ArticleDetailViewModel> { ArticleDetailViewModel(get(), get()) }
}
