package com.on.turip.di

import com.on.turip.data.userStorage.DefaultUserStorage
import com.on.turip.data.userStorage.UserStorage

object LocalStorageModule {
    val userStorage: UserStorage by lazy {
        DefaultUserStorage(ApplicationContextProvider.applicationContext)
    }
}
