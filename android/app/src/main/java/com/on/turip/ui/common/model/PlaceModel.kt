package com.on.turip.ui.common.model

data class PlaceModel(
    val name: String,
    val category: String,
    val mapLink: String,
)

fun getPlaceDummyWithDay(): Map<DayModel, List<PlaceModel>> {
    val dayOne = DayModel(1)
    val dayTwo = DayModel(2)
    val dayThree = DayModel(3)
    return mapOf(
        dayOne to
            listOf(
                PlaceModel(
                    "경포도립공원",
                    "관광지",
                    """https://map.naver.com/p/
                        |entry/place/13300191?c=15.71,0,0,
                        |0,dh&placePath=/home?from=map&fromPan
                        |elNum=1&additionalHeight=76&timestamp=202
                        |507222020&locale=ko&svcName=map_pcv5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "최일순 짬뽕순두부",
                    "음식점",
                    """https://map.naver.com/p/ent
                        |ry/place/1161455816?c=15.71,0,0,0,d
                        |h&placePath=/home?from=map&fromPanelNu
                        |m=1&additionalHeight=76&timestamp=2025072
                        |22019&locale=ko&svcName=map_pcv5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장3",
                    "관광지",
                    """https://map.naver.com/p/
                        |search/%EA%B2%BD%ED%8F%AC%EB%8C%80%
                        |20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/
                        |place/12079976?c=15.00,0,0,0,dh&placePath=/ho
                        |me?entry=bmp&from=map&fromPanelNum=2&timestamp=
                        |202507222010&locale=ko&svcName=map_pcv5&searchText
                        |=%EA%B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%9
                        |5%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장4",
                    "관광지",
                    """https://map.naver.com/p/
                        |search/%EA%B2%BD%ED%8F%AC%EB%8C%80%
                        |20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/
                        |place/12079976?c=15.00,0,0,0,dh&placePath=/ho
                        |me?entry=bmp&from=map&fromPanelNum=2&timestamp=
                        |202507222010&locale=ko&svcName=map_pcv5&searchText
                        |=%EA%B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%9
                        |5%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장5",
                    "관광지",
                    """https://map.naver.com/p/
                        |search/%EA%B2%BD%ED%8F%AC%EB%8C%80%
                        |20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/
                        |place/12079976?c=15.00,0,0,0,dh&placePath=/ho
                        |me?entry=bmp&from=map&fromPanelNum=2&timestamp=
                        |202507222010&locale=ko&svcName=map_pcv5&searchText
                        |=%EA%B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%9
                        |5%EC%9E%A5
                    """.trimMargin(),
                ),
            ),
        dayTwo to
            listOf(
                PlaceModel(
                    "2페이지 경포대 해수욕장",
                    "관광지",
                    """https://map.naver.com/p/
                        |search/%EA%B2%BD%ED%8F%AC%EB%8C%80%
                        |20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/
                        |place/12079976?c=15.00,0,0,0,dh&placePath=/ho
                        |me?entry=bmp&from=map&fromPanelNum=2&timestamp=
                        |202507222010&locale=ko&svcName=map_pcv5&searchText
                        |=%EA%B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%9
                        |5%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포동해횟집",
                    "음식점",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장3",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장4",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장5",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
            ),
        dayThree to
            listOf(
                PlaceModel(
                    "3페이지 경포대 해수욕장",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포동해횟집",
                    "음식점",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장3",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장4",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
                PlaceModel(
                    "경포대 해수욕장5",
                    "관광지",
                    """https://map.naver.com/p/search/%EA%
                        |B2%BD%ED%8F%AC%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/12079976?c=15.00,0,0,0,dh&placePath=/home?entry=bm
                        |&from=map&fromPanelNum=2&timestamp=202507222010&locale=ko&svcName=map_pcv5&se
                        |archText=%EA%B2%BD%ED%8F%AC%EB%8C%80
                        |%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5
                    """.trimMargin(),
                ),
            ),
    )
}
