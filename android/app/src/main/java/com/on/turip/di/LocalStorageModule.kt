package com.on.turip.di

import com.on.turip.data.database.TuripDatabase
import com.on.turip.data.userstorage.DefaultUserStorage
import com.on.turip.data.userstorage.UserStorage

object LocalStorageModule {
    val userStorage: UserStorage by lazy {
        DefaultUserStorage(ApplicationContextProvider.applicationContext)
    }
    private val turipDatabase by lazy {
        TuripDatabase.getDataBase(ApplicationContextProvider.applicationContext)
    }
    val turipDao by lazy {
        turipDatabase.searchHistoryDao()
    }
}
