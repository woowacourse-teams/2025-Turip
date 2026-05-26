package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.content.ContentResponse
import com.on.turip.core.network.dto.content.UsersLikeContentResponse

interface ContentDatasource {
    suspend fun getContent(contentId: Long): ContentResponse
    suspend fun getPopularContents(size: Int): List<UsersLikeContentResponse>
    suspend fun getContentsByKeyword(keyword: String, size: Int, lastId: Long): List<ContentResponse>
    suspend fun getContentsSizeByKeyword(keyword: String): Int
    suspend fun getContentsByRegion(regionCategory: String, size: Int, lastId: Long): List<ContentResponse>
    suspend fun getContentsSizeByRegion(regionCategory: String): Int
}
