package com.on.turip.feature.regionbriefing.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.component.TuripLoadingIndicator
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.all_text_expand
import com.on.turip.core.designsystem.generated.resources.briefing_related_spot_empty
import com.on.turip.core.designsystem.generated.resources.briefing_related_spot_error
import com.on.turip.core.designsystem.generated.resources.briefing_related_spot_title
import com.on.turip.core.designsystem.generated.resources.briefing_related_spot_unsupported
import com.on.turip.core.designsystem.generated.resources.briefing_video_count
import com.on.turip.core.designsystem.generated.resources.briefing_video_error
import com.on.turip.core.designsystem.generated.resources.briefing_video_title
import com.on.turip.core.designsystem.generated.resources.region_briefing_empty_video
import com.on.turip.core.designsystem.generated.resources.retry
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.BriefingErrorView
import com.on.turip.core.ui.component.BriefingNotice
import com.on.turip.core.ui.component.BriefingNoticeText
import com.on.turip.core.ui.component.BriefingPlaceholder
import com.on.turip.core.ui.component.BriefingSectionHeader
import com.on.turip.core.ui.component.RelatedSpotItem
import com.on.turip.core.ui.component.VideoSummaryItem
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.content.VideoSummaryModel
import com.on.turip.core.ui.model.region.RelatedSpotModel
import com.on.turip.core.ui.model.region.RelatedSpotsUiState
import com.on.turip.core.ui.util.baseMonthText
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.regionbriefing.impl.component.RegionVisitorCard
import com.on.turip.feature.regionbriefing.impl.component.RelatedSpotListSkeleton
import com.on.turip.feature.regionbriefing.impl.component.VideoListSkeleton
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * 인기 관광지 지도에서 고른 지역 한 곳의 브리핑.
 *
 * 랜덤 여행 브리핑과 본문 구성이 같지만 뽑기 맥락(티켓·재추첨·튜립 만들기)이 없다.
 * 영상은 고르는 단계 없이 탭하면 곧장 영상 상세로 간다.
 *
 * @param visitorCount 지도가 이미 조회해 둔 값. 이 화면은 방문자 수를 다시 조회하지 않는다.
 * @param baseMonth 방문자 수 기준월(`yyyyMM`)
 */
@Composable
fun RegionBriefingScreen(
    regionCategoryName: String,
    visitorCount: Long,
    baseMonth: String?,
    onBackClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onRelatedSpotClick: (regionCategoryName: String, spotCategory: String) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionBriefingViewModel = koinViewModel(),
) {
    val uiState: RegionBriefingState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(regionCategoryName, visitorCount, baseMonth) {
        viewModel.initRegion(
            regionCategoryName = regionCategoryName,
            visitorCount = visitorCount,
            baseMonth = baseMonth,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: RegionBriefingEffect ->
            when (effect) {
                is RegionBriefingEffect.NavigateToTripDetail -> onContentClick(effect.contentId)

                is RegionBriefingEffect.NavigateToRelatedSpotDetail -> {
                    onRelatedSpotClick(effect.regionCategoryName, effect.spotCategory)
                }

                RegionBriefingEffect.NavigateToLogin -> onNavigateToLoginScreen()
            }
        }
    }

    RegionBriefingContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun RegionBriefingContent(
    uiState: RegionBriefingState,
    onIntent: (RegionBriefingIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 이미 받아온 페이지를 먼저 로컬로 펼치고, 다 펼친 뒤에야 다음 페이지를 서버에 요청한다.
    var isVideoListExpanded: Boolean by rememberSaveable(uiState.regionName) {
        mutableStateOf(false)
    }
    val baseMonthLabel: String = baseMonthText(uiState.baseMonth)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
    ) {
        TuripAppBar(
            start = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(APP_BAR_ICON_SIZE),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.all_back_description),
                        tint = TuripTheme.colors.black,
                    )
                }
            },
            center = {
                Text(
                    text = uiState.regionName,
                    style = TuripTheme.typography.title1,
                    color = TuripTheme.colors.black,
                )
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = TuripTheme.spacing.extraLarge,
                    end = TuripTheme.spacing.extraLarge,
                    top = TuripTheme.spacing.small,
                    bottom = TuripTheme.spacing.extraLarge,
                ),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
        ) {
            item(key = VISITOR_ITEM_KEY) {
                RegionVisitorCard(
                    baseMonthText = baseMonthLabel,
                    visitorCountText = uiState.visitorCountText,
                )
            }

            item(key = VIDEO_TITLE_ITEM_KEY) {
                BriefingSectionHeader(
                    title =
                        stringResource(Res.string.briefing_video_title)
                            .formatResource(uiState.regionName),
                    trailingText =
                        stringResource(Res.string.briefing_video_count)
                            .formatResource(uiState.videoCount)
                            .takeIf { uiState.isVideoListFetched && uiState.videos.isNotEmpty() },
                )
            }

            videoSection(
                uiState = uiState,
                isVideoListExpanded = isVideoListExpanded,
                onExpandClick = { isVideoListExpanded = true },
                onIntent = onIntent,
            )

            item(key = RELATED_SPOT_TITLE_ITEM_KEY) {
                BriefingSectionHeader(
                    title =
                        stringResource(Res.string.briefing_related_spot_title)
                            .formatResource(uiState.regionName),
                    modifier = Modifier.padding(top = TuripTheme.spacing.small),
                )
            }

            relatedSpotSection(
                relatedSpotsUiState = uiState.relatedSpotsUiState,
                onIntent = onIntent,
            )
        }
    }
}

/**
 * 영상 섹션. 목록 대신 로딩·에러·빈 상태를 보여주는 경우가 많아 한 덩어리로 묶는다.
 */
private fun LazyListScope.videoSection(
    uiState: RegionBriefingState,
    isVideoListExpanded: Boolean,
    onExpandClick: () -> Unit,
    onIntent: (RegionBriefingIntent) -> Unit,
) {
    when {
        uiState.isVideoListLoading -> {
            item(key = VIDEO_LOADING_ITEM_KEY) {
                VideoListSkeleton()
            }
        }

        uiState.videoErrorUiState != ErrorUiState.None -> {
            item(key = VIDEO_ERROR_ITEM_KEY) {
                BriefingErrorView(
                    message = stringResource(Res.string.briefing_video_error),
                    onRetryClick = { onIntent(RegionBriefingIntent.RetryVideos) },
                )
            }
        }

        uiState.shouldShowEmptyVideos -> {
            item(key = VIDEO_EMPTY_ITEM_KEY) {
                BriefingPlaceholder {
                    Text(
                        text = stringResource(Res.string.region_briefing_empty_video),
                        style = TuripTheme.typography.body1,
                        color = TuripTheme.colors.gray03,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        else -> {
            val visibleVideos =
                if (isVideoListExpanded) uiState.videos else uiState.videos.take(VIDEO_PREVIEW_COUNT)

            items(
                items = visibleVideos,
                key = { it.contentId },
            ) { video ->
                VideoSummaryItem(
                    video = video,
                    onClick = { onIntent(RegionBriefingIntent.ClickVideo(video.contentId)) },
                )
            }

            val hasMoreToReveal: Boolean =
                !isVideoListExpanded && uiState.videos.size > VIDEO_PREVIEW_COUNT
            val hasMoreToFetch: Boolean = isVideoListExpanded && uiState.isVideoListLoadable

            if (hasMoreToReveal || hasMoreToFetch) {
                item(key = VIDEO_MORE_ITEM_KEY) {
                    TextButton(
                        onClick = {
                            if (hasMoreToReveal) {
                                onExpandClick()
                            } else {
                                onIntent(RegionBriefingIntent.LoadMoreVideos)
                            }
                        },
                        enabled = !uiState.isLoadingMoreVideos,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (uiState.isLoadingMoreVideos) {
                            TuripLoadingIndicator(size = MORE_BUTTON_LOADING_SIZE)
                        } else {
                            Text(
                                text = stringResource(Res.string.all_text_expand),
                                style = TuripTheme.typography.body2,
                                color = TuripTheme.colors.gray03,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.relatedSpotSection(
    relatedSpotsUiState: RelatedSpotsUiState,
    onIntent: (RegionBriefingIntent) -> Unit,
) {
    when (relatedSpotsUiState) {
        RelatedSpotsUiState.Loading -> {
            item(key = RELATED_SPOT_LOADING_ITEM_KEY) {
                RelatedSpotListSkeleton()
            }
        }

        RelatedSpotsUiState.Unsupported -> {
            item(key = RELATED_SPOT_NOTICE_ITEM_KEY) {
                BriefingNotice {
                    BriefingNoticeText(stringResource(Res.string.briefing_related_spot_unsupported))
                }
            }
        }

        RelatedSpotsUiState.Error -> {
            item(key = RELATED_SPOT_ERROR_ITEM_KEY) {
                BriefingNotice {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BriefingNoticeText(stringResource(Res.string.briefing_related_spot_error))

                        TextButton(
                            onClick = { onIntent(RegionBriefingIntent.RetryRelatedSpots) },
                        ) {
                            Text(
                                text = stringResource(Res.string.retry),
                                style = TuripTheme.typography.body2,
                                color = TuripTheme.colors.primary,
                            )
                        }
                    }
                }
            }
        }

        is RelatedSpotsUiState.Success -> {
            if (relatedSpotsUiState.isEmpty) {
                item(key = RELATED_SPOT_EMPTY_ITEM_KEY) {
                    BriefingNotice {
                        BriefingNoticeText(stringResource(Res.string.briefing_related_spot_empty))
                    }
                }
            } else {
                items(
                    items = relatedSpotsUiState.relatedSpots,
                    key = { it.category },
                ) { relatedSpot ->
                    RelatedSpotItem(
                        relatedSpot = relatedSpot,
                        onClick = {
                            onIntent(RegionBriefingIntent.ClickRelatedSpot(relatedSpot.category))
                        },
                    )
                }
            }
        }
    }
}

private val APP_BAR_ICON_SIZE = 36.dp
private val MORE_BUTTON_LOADING_SIZE = 16.dp
private const val VIDEO_PREVIEW_COUNT: Int = 3
private const val VISITOR_ITEM_KEY: String = "visitor"
private const val VIDEO_TITLE_ITEM_KEY: String = "video_title"
private const val VIDEO_MORE_ITEM_KEY: String = "video_more"
private const val VIDEO_LOADING_ITEM_KEY: String = "video_loading"
private const val VIDEO_ERROR_ITEM_KEY: String = "video_error"
private const val VIDEO_EMPTY_ITEM_KEY: String = "video_empty"
private const val RELATED_SPOT_TITLE_ITEM_KEY: String = "related_spot_title"
private const val RELATED_SPOT_LOADING_ITEM_KEY: String = "related_spot_loading"
private const val RELATED_SPOT_NOTICE_ITEM_KEY: String = "related_spot_notice"
private const val RELATED_SPOT_EMPTY_ITEM_KEY: String = "related_spot_empty"
private const val RELATED_SPOT_ERROR_ITEM_KEY: String = "related_spot_error"

@Preview(showBackground = true, name = "지역 브리핑")
@Composable
private fun RegionBriefingPreview() {
    TuripTheme {
        RegionBriefingContent(
            uiState =
                RegionBriefingState(
                    regionName = "강릉",
                    visitorCountText = "480만",
                    baseMonth = "202506",
                    videos =
                        persistentListOf(
                            VideoSummaryModel(
                                contentId = 1L,
                                title = "강릉 바다 따라 힐링 여행 브이로그",
                                thumbnailUrl = "",
                                channelName = "바다좋아 튜립팀",
                                profileImageUrl = "",
                                uploadedDate = "2026-05-12",
                                cityName = "강릉",
                                nights = 2,
                                days = 3,
                                placeCount = 15,
                            ),
                        ),
                    isVideoListLoading = false,
                    isVideoListFetched = true,
                    relatedSpotsUiState =
                        RelatedSpotsUiState.Success(
                            persistentListOf(
                                RelatedSpotModel(
                                    category = "관광지",
                                    spots = persistentListOf("경포대", "안목해변", "오죽헌"),
                                ),
                                RelatedSpotModel(
                                    category = "음식",
                                    spots = persistentListOf("초당순두부", "중앙시장"),
                                ),
                            ),
                        ),
                ),
            onIntent = {},
            onBackClick = {},
        )
    }
}
