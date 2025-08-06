package com.on.turip.data.region

import com.on.turip.domain.region.Country
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.RegionRepository

class DefaultRegionRepository : RegionRepository {
    override suspend fun loadRegionCategories(isKorea: Boolean): Result<List<RegionCategory>> =
        if (isKorea) {
            Result.success(
                listOf(
                    RegionCategory(
                        name = "서울",
                        imageUrl =
                            """
                            https://img.freepik.com/free-photo/seoul-cityscape-twilight-south-korea_335224-273.jpg?semt=ais_hybrid&w=740
                            """.trimIndent(),
                        country =
                            Country(
                                id = 1,
                                name = "대한민국",
                                imageUrl =
                                    """
                                    https://i.namu.wiki/i/2ySwTDCC04jQeqKZEZ-HkeIdT91knN8WvOIiwkQZN-Gh5wG6e7PpdxJHQ73Lx_T5E_ZNByh1jAMJY4kbKL-oIg.svg
                                    """.trimIndent(),
                            ),
                    ),
                    RegionCategory(
                        name = "부산",
                        imageUrl = "https://img.freepik.com/premium-photo/busan-cityscape-south-korea_31965-6471.jpg",
                        country =
                            Country(
                                id = 1,
                                name = "대한민국",
                                imageUrl =
                                    """
                                    https://i.namu.wiki/i/2ySwTDCC04jQeqKZEZ-HkeIdT91knN8WvOIiwkQZN-Gh5wG6e7PpdxJHQ73Lx_T5E_ZNByh1jAMJY4kbKL-oIg.svg
                                    """.trimIndent(),
                            ),
                    ),
                    RegionCategory(
                        name = "제주",
                        imageUrl = "https://img.freepik.com/free-photo/aerial-view-jeju-island_181624-34451.jpg",
                        country =
                            Country(
                                id = 1,
                                name = "대한민국",
                                imageUrl =
                                    """
                                    https://i.namu.wiki/i/2ySwTDCC04jQeqKZEZ-HkeIdT91knN8WvOIiwkQZN-Gh5wG6e7PpdxJHQ73Lx_T5E_ZNByh1jAMJY4kbKL-oIg.svg
                                    """.trimIndent(),
                            ),
                    ),
                ),
            )
        } else {
            Result.success(
                listOf(
                    RegionCategory(
                        name = "도쿄",
                        imageUrl = "https://img.freepik.com/free-photo/tokyo-tower-cityscape-night-japan_335224-300.jpg",
                        country =
                            Country(
                                id = 2,
                                name = "일본",
                                imageUrl = "https://upload.wikimedia.org/wikipedia/en/9/9e/Flag_of_Japan.svg",
                            ),
                    ),
                    RegionCategory(
                        name = "파리",
                        imageUrl = "https://img.freepik.com/free-photo/eiffel-tower-paris-sunset-france_181624-22663.jpg",
                        country =
                            Country(
                                id = 3,
                                name = "프랑스",
                                imageUrl = "https://upload.wikimedia.org/wikipedia/en/c/c3/Flag_of_France.svg",
                            ),
                    ),
                    RegionCategory(
                        name = "뉴욕",
                        imageUrl = "https://img.freepik.com/free-photo/aerial-view-manhattan-new-york-city_181624-6011.jpg",
                        country =
                            Country(
                                id = 4,
                                name = "미국",
                                imageUrl = "https://upload.wikimedia.org/wikipedia/en/a/a4/Flag_of_the_United_States.svg",
                            ),
                    ),
                ),
            )
        } // TODO: 추후 더미 제거 후 API 연결 예정
}
