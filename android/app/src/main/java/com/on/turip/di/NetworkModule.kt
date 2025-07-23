package com.on.turip.di

import com.on.turip.data.contents.service.ContentsService
import com.on.turip.data.network.NetworkClient

object NetworkModule {
    val contentsService: ContentsService by lazy {
        NetworkClient.turipNetwork.create(ContentsService::class.java)
    }
}
