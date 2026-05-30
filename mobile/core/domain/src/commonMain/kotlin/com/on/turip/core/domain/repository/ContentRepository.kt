package com.on.turip.core.domain.repository

interface ContentRepository {
    suspend fun getContent(contentId: Long): Result<Content>

    suspend fun loadPopularFavoriteContents(size: Int): Result<List<UsersLikeContent>>

    suspend fun loadContentsByKeyword(keyword: String, size: Int, lastId: Long): Result<List<Content>>

    suspend fun loadContentsSizeByKeyword(keyword: String): Result<Int>

    suspend fun loadContentsByRegion(regionCategory: String, size: Int, lastId: Long): Result<List<Content>>

    suspend fun loadContentsSizeByRegion(regionCategory: String): Result<Int>
}
