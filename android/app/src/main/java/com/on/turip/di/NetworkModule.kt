package com.on.turip.di

import com.on.turip.data.content.place.service.ContentPlaceService
import com.on.turip.data.content.service.ContentService
import com.on.turip.data.creator.service.CreatorService
import com.on.turip.data.favorite.service.FavoriteService
import com.on.turip.data.folder.service.FolderService
import com.on.turip.data.network.NetworkClient
import com.on.turip.data.place.service.PlaceService
import com.on.turip.data.region.service.RegionService

object NetworkModule {
    val contentService: ContentService by lazy {
        NetworkClient.turipNetwork.create(ContentService::class.java)
    }
    val creatorService: CreatorService by lazy {
        NetworkClient.turipNetwork.create(CreatorService::class.java)
    }
    val contentPlaceService: ContentPlaceService by lazy {
        NetworkClient.turipNetwork.create(ContentPlaceService::class.java)
    }
    val regionService: RegionService by lazy {
        NetworkClient.turipNetwork.create(RegionService::class.java)
    }
    val favoriteService: FavoriteService by lazy {
        NetworkClient.turipNetwork.create(FavoriteService::class.java)
    }
    val folderService: FolderService by lazy {
        NetworkClient.turipNetwork.create(FolderService::class.java)
    }
    val favoritePlaceService: PlaceService by lazy {
        NetworkClient.turipNetwork.create(PlaceService::class.java)
    }
}
