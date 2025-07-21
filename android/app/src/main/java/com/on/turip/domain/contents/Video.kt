package com.on.turip.domain.contents

data class Video(
    val content: Content,
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
) {
    companion object {
        val dummyVideos =
            listOf(
                Video(
                    content =
                        Content(
                            id = 1,
                            creator =
                                Creator(
                                    id = 101,
                                    channelName = "여행하는 뭉치",
                                    profileImage = "http://turip.com/static/youtuber1",
                                ),
                            title = "느좋 감성 대구 여행 어쩌구저쩌구",
                            url = "https://youtu.be/dummy1",
                            uploadedDate = "2025-07-15",
                        ),
                    tripDuration =
                        TripDuration(
                            nights = 2,
                            days = 3,
                        ),
                    tripPlaceCount = 5,
                ),
                Video(
                    content =
                        Content(
                            id = 2,
                            creator =
                                Creator(
                                    id = 102,
                                    channelName = "뚜벅이의 하루",
                                    profileImage = "http://turip.com/static/youtuber2",
                                ),
                            title = "제주도 혼자 여행 브이로그",
                            url = "https://youtu.be/dummy2",
                            uploadedDate = "2025-06-28",
                        ),
                    tripDuration =
                        TripDuration(
                            nights = 3,
                            days = 4,
                        ),
                    tripPlaceCount = 8,
                ),
                Video(
                    content =
                        Content(
                            id = 3,
                            creator =
                                Creator(
                                    id = 103,
                                    channelName = "감성캠퍼",
                                    profileImage = "http://turip.com/static/youtuber3",
                                ),
                            title = "강원도 캠핑 2박 3일 여행",
                            url = "https://youtu.be/dummy3",
                            uploadedDate = "2025-05-10",
                        ),
                    tripDuration =
                        TripDuration(
                            nights = 2,
                            days = 3,
                        ),
                    tripPlaceCount = 4,
                ),
            )
    }
}
