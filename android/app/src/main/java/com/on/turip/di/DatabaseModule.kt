package com.on.turip.di

import com.on.turip.data.database.TuripDatabase

object DatabaseModule {
    private val turipDatabase by lazy {
        TuripDatabase.getDataBase(ApplicationContextProvider.applicationContext)
    }

    val turipDao by lazy {
        turipDatabase.searchHistoryDao()
    }
}
