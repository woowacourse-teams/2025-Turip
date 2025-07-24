package com.on.turip.di

import com.on.turip.data.content.service.ContentService
import com.on.turip.data.creator.service.CreatorService
import com.on.turip.data.network.NetworkClient
import com.on.turip.data.trip.service.TripService

object NetworkModule {
    val contentService: ContentService by lazy {
        NetworkClient.turipNetwork.create(ContentService::class.java)
    }
    val creatorService: CreatorService by lazy {
        NetworkClient.turipNetwork.create(CreatorService::class.java)
    }
    val tripService: TripService by lazy {
        NetworkClient.turipNetwork.create(TripService::class.java)
    }
}
