package com.on.turip.di

import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.datasource.DefaultContentRemoteDataSource
import com.on.turip.data.content.place.datasource.ContentPlaceRemoteDataSource
import com.on.turip.data.content.place.datasource.DefaultContentPlaceRemoteDataSource
import com.on.turip.data.creator.datasource.CreatorRemoteDataSource
import com.on.turip.data.creator.datasource.DefaultCreatorRemoteDataSource
import com.on.turip.data.favorite.datasource.DefaultFavoriteRemoteDataSource
import com.on.turip.data.favorite.datasource.FavoriteRemoteDataSource
import com.on.turip.data.folder.datasource.DefaultFolderRemoteDataSource
import com.on.turip.data.folder.datasource.FolderRemoteDataSource
import com.on.turip.data.place.datasource.DefaultFavoritePlaceRemoteDataSource
import com.on.turip.data.place.datasource.FavoritePlaceRemoteDataSource
import com.on.turip.data.region.datasource.DefaultRegionRemoteDataSource
import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.searchhistory.datasource.DefaultSearchHistoryDataSource
import com.on.turip.data.searchhistory.datasource.SearchHistoryDataSource
import com.on.turip.data.userstorage.datasource.DefaultUserStorageLocalDataSource
import com.on.turip.data.userstorage.datasource.UserStorageLocalDataSource

object DataSourceModule {
    val contentRemoteDataSource: ContentRemoteDataSource by lazy {
        DefaultContentRemoteDataSource(NetworkModule.contentService)
    }
    val creatorRemoteDataSource: CreatorRemoteDataSource by lazy {
        DefaultCreatorRemoteDataSource(NetworkModule.creatorService)
    }
    val contentPlaceRemoteDataSource: ContentPlaceRemoteDataSource by lazy {
        DefaultContentPlaceRemoteDataSource(NetworkModule.contentPlaceService)
    }
    val userStorageLocalDataSource: UserStorageLocalDataSource by lazy {
        DefaultUserStorageLocalDataSource(LocalStorageModule.userStorage)
    }
    val favoriteRemoteDataSource: FavoriteRemoteDataSource by lazy {
        DefaultFavoriteRemoteDataSource(NetworkModule.favoriteService)
    }
    val regionRemoteDataSource: RegionRemoteDataSource by lazy {
        DefaultRegionRemoteDataSource(NetworkModule.regionService)
    }
    val searchHistoryDataSource: SearchHistoryDataSource by lazy {
        DefaultSearchHistoryDataSource(LocalStorageModule.turipDao)
    }
    val folderRemoteDataSource: FolderRemoteDataSource by lazy {
        DefaultFolderRemoteDataSource(NetworkModule.folderService)
    }
    val favoritePlaceRemoteDataSource: FavoritePlaceRemoteDataSource by lazy {
        DefaultFavoritePlaceRemoteDataSource(NetworkModule.favoritePlaceService)
    }
}
