package com.on.turip.di

import com.on.turip.data.InMemoryMuseumStorage
import com.on.turip.data.KtorMuseumApi
import com.on.turip.data.MuseumApi
import com.on.turip.data.MuseumRepository
import com.on.turip.data.MuseumStorage
import com.on.turip.screens.detail.DetailViewModel
import com.on.turip.screens.list.ListViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule =
    module {
        single<MuseumApi> { KtorMuseumApi() }
        single<MuseumStorage> { InMemoryMuseumStorage() }
        single {
            MuseumRepository(get(), get()).apply {
                initialize()
            }
        }
    }

val viewModelModule =
    module {
        factoryOf(::ListViewModel)
        factoryOf(::DetailViewModel)
    }

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
