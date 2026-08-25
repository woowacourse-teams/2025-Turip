package com.on.turip.feature.randomtravel.impl

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.component.TuripLoadingIndicator
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.all_text_expand
import com.on.turip.core.designsystem.generated.resources.random_travel_briefing_error
import com.on.turip.core.designsystem.generated.resources.random_travel_briefing_related_spot_title
import com.on.turip.core.designsystem.generated.resources.random_travel_briefing_video_count
import com.on.turip.core.designsystem.generated.resources.random_travel_briefing_video_title
import com.on.turip.core.designsystem.generated.resources.random_travel_confirmed_label
import com.on.turip.core.designsystem.generated.resources.random_travel_confirmed_title
import com.on.turip.core.designsystem.generated.resources.random_travel_empty_video
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_empty
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_error
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_unsupported
import com.on.turip.core.designsystem.generated.resources.random_travel_reroll
import com.on.turip.core.designsystem.generated.resources.random_travel_select_video_guide
import com.on.turip.core.designsystem.generated.resources.random_travel_spinning_title
import com.on.turip.core.designsystem.generated.resources.random_travel_start_trip
import com.on.turip.core.designsystem.generated.resources.random_travel_title
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_create_failed
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_created
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_created_partial
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_place_failed
import com.on.turip.core.designsystem.generated.resources.retry
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.randomtravel.impl.component.PrintedTicket
import com.on.turip.feature.randomtravel.impl.component.RandomTravelRelatedSpotItem
import com.on.turip.feature.randomtravel.impl.component.RandomTravelVideoItem
import com.on.turip.feature.randomtravel.impl.component.SlotMachineReel
import com.on.turip.feature.randomtravel.impl.component.SlotReelPhase
import com.on.turip.feature.randomtravel.impl.component.TuripDraftBottomSheet
import com.on.turip.feature.randomtravel.impl.model.RandomDestinationModel
import com.on.turip.feature.randomtravel.impl.model.RandomTravelRelatedSpotModel
import com.on.turip.feature.randomtravel.impl.platform.rememberReduceMotionEnabled
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RandomTravelScreen(
    onBackClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onRelatedSpotClick: (regionCategoryName: String, spotCategory: String) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RandomTravelViewModel = koinViewModel(),
) {
    val uiState: RandomTravelState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: RandomTravelEffect ->
            when (effect) {
                RandomTravelEffect.PerformDestinationHaptic ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                is RandomTravelEffect.NavigateToTripDetail -> onContentClick(effect.contentId)
                RandomTravelEffect.NavigateToLogin -> onNavigateToLoginScreen()

                // 스낵바 호스트가 앱 전역이라 영상 상세로 이동한 뒤에도 결과가 그대로 보인다.
                is RandomTravelEffect.ShowTuripCreated ->
                    snackbarDelegate.showSnackbar(
                        message =
                            when {
                                effect.savedCount == 0 ->
                                    getString(
                                        Res.string.random_travel_turip_place_failed,
                                        effect.turipName,
                                    )

                                effect.savedCount == effect.requestedCount ->
                                    getString(
                                        Res.string.random_travel_turip_created,
                                        effect.turipName,
                                        effect.savedCount,
                                    )

                                else ->
                                    getString(
                                        Res.string.random_travel_turip_created_partial,
                                        effect.turipName,
                                        effect.savedCount,
                                    )
                            },
                    )

                RandomTravelEffect.ShowTuripCreateFailed ->
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.random_travel_turip_create_failed),
                    )
            }
        }
    }

    RandomTravelContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        onRelatedSpotClick = onRelatedSpotClick,
        modifier = modifier,
    )

    TuripDraftBottomSheet(
        turipDraftUiState = uiState.turipDraftUiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun RandomTravelContent(
    uiState: RandomTravelState,
    onIntent: (RandomTravelIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRelatedSpotClick: (regionCategoryName: String, spotCategory: String) -> Unit = { _, _ -> },
) {
    val reduceMotion: Boolean = rememberReduceMotionEnabled()

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
                    text = stringResource(Res.string.random_travel_title),
                    style = TuripTheme.typography.title1,
                    color = TuripTheme.colors.black,
                )
            },
        )

        when {
            uiState.shouldShowErrorScreen -> {
                ErrorScreen(
                    errorUiState = uiState.errorUiState,
                    onRetryClick = { onIntent(RandomTravelIntent.RetryDraw) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.phase == RandomTravelPhase.Briefing -> {
                BriefingSection(
                    uiState = uiState,
                    reduceMotion = reduceMotion,
                    onIntent = onIntent,
                    onRelatedSpotClick = onRelatedSpotClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Preparing(결과를 모른 채 미리 회전) / Spinning / Confirmed 모두 릴을 그린다.
            else -> {
                SlotSection(
                    uiState = uiState,
                    reduceMotion = reduceMotion,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun SlotSection(
    uiState: RandomTravelState,
    reduceMotion: Boolean,
    onIntent: (RandomTravelIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 결과를 아직 모르는 동안(Preparing/Spinning)은 같은 "찾는 중" 문구·스타일을 보여준다.
    val isDeciding: Boolean =
        uiState.phase == RandomTravelPhase.Preparing || uiState.phase == RandomTravelPhase.Spinning

    val reelPhase: SlotReelPhase =
        when (uiState.phase) {
            RandomTravelPhase.Preparing -> SlotReelPhase.Idle
            RandomTravelPhase.Spinning -> SlotReelPhase.Spinning
            else -> SlotReelPhase.Stopped
        }

    // 확정되는 순간 살짝 커지며 테두리에 색이 들어와 "멈췄다"는 신호를 준다.
    val reelScale: Float by animateFloatAsState(
        targetValue = if (isDeciding) 1f else CONFIRMED_REEL_SCALE,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    )
    val reelBorderColor: Color by animateColorAsState(
        targetValue = if (isDeciding) TuripTheme.colors.border else TuripTheme.colors.primary,
    )

    Column(
        modifier = modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
                if (isDeciding) {
                    stringResource(Res.string.random_travel_spinning_title)
                } else {
                    stringResource(Res.string.random_travel_confirmed_label)
                },
            style = TuripTheme.typography.title1,
            color = TuripTheme.colors.gray04,
            textAlign = TextAlign.Center,
        )

        Text(
            text = if (isDeciding) "" else stringResource(Res.string.random_travel_confirmed_title),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))

        SlotMachineReel(
            reelNames = uiState.reelNames,
            reelPhase = reelPhase,
            reduceMotion = reduceMotion,
            onSpinFinished = { onIntent(RandomTravelIntent.FinishSlot) },
            borderColor = reelBorderColor,
            modifier =
                Modifier.graphicsLayer {
                    scaleX = reelScale
                    scaleY = reelScale
                },
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))
    }
}

@Composable
private fun BriefingSection(
    uiState: RandomTravelState,
    reduceMotion: Boolean,
    onIntent: (RandomTravelIntent) -> Unit,
    onRelatedSpotClick: (regionCategoryName: String, spotCategory: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val destination: RandomDestinationModel = uiState.destination ?: return
    var isVideoListExpanded: Boolean by rememberSaveable(destination.name) { mutableStateOf(false) }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding =
                PaddingValues(
                    start = TuripTheme.spacing.extraLarge,
                    end = TuripTheme.spacing.extraLarge,
                    top = TuripTheme.spacing.small,
                    bottom = TuripTheme.spacing.extraLarge,
                ),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
        ) {
            item(key = TICKET_ITEM_KEY) {
                PrintedTicket(
                    destination = destination,
                    reduceMotion = reduceMotion,
                )
            }

            item(key = VIDEO_TITLE_ITEM_KEY) {
                BriefingSectionHeader(
                    title =
                        stringResource(Res.string.random_travel_briefing_video_title)
                            .formatResource(destination.name),
                    trailingText =
                        stringResource(Res.string.random_travel_briefing_video_count)
                            .formatResource(destination.videoCount)
                            .takeIf { uiState.isBriefingFetched && uiState.videos.isNotEmpty() },
                )
            }

            when {
                uiState.isBriefingLoading -> {
                    item(key = BRIEFING_LOADING_ITEM_KEY) {
                        BriefingPlaceholder {
                            TuripLoadingIndicator()
                        }
                    }
                }

                uiState.briefingErrorUiState != ErrorUiState.None -> {
                    item(key = BRIEFING_ERROR_ITEM_KEY) {
                        BriefingErrorView(
                            onRetryClick = { onIntent(RandomTravelIntent.RetryBriefing) },
                        )
                    }
                }

                uiState.shouldShowEmptyBriefing -> {
                    item(key = BRIEFING_EMPTY_ITEM_KEY) {
                        BriefingPlaceholder {
                            Text(
                                text = stringResource(Res.string.random_travel_empty_video),
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
                        RandomTravelVideoItem(
                            video = video,
                            isSelected = video.contentId == uiState.selectedContentId,
                            onClick = {
                                onIntent(RandomTravelIntent.SelectVideo(video.contentId))
                            },
                        )
                    }

                    // 먼저 이미 받아온 페이지를 로컬로 펼치고, 다 펼친 뒤에야 다음 페이지를 서버에 요청한다.
                    val hasMoreToReveal: Boolean =
                        !isVideoListExpanded && uiState.videos.size > VIDEO_PREVIEW_COUNT
                    val hasMoreToFetch: Boolean = isVideoListExpanded && uiState.isVideoListLoadable

                    if (hasMoreToReveal || hasMoreToFetch) {
                        item(key = VIDEO_MORE_ITEM_KEY) {
                            TextButton(
                                onClick = {
                                    if (hasMoreToReveal) {
                                        isVideoListExpanded = true
                                    } else {
                                        onIntent(RandomTravelIntent.LoadMoreVideos)
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

            // HTML 프로토타입의 `오늘 열리는 행사` 자리 — 영상 섹션 바로 아래에 놓는다.
            item(key = RELATED_SPOT_TITLE_ITEM_KEY) {
                BriefingSectionHeader(
                    title =
                        stringResource(Res.string.random_travel_briefing_related_spot_title)
                            .formatResource(destination.name),
                    modifier = Modifier.padding(top = TuripTheme.spacing.small),
                )
            }

            when (val relatedSpotsUiState = uiState.relatedSpotsUiState) {
                RelatedSpotsUiState.Loading -> {
                    item(key = RELATED_SPOT_LOADING_ITEM_KEY) {
                        BriefingNotice { TuripLoadingIndicator() }
                    }
                }

                RelatedSpotsUiState.Unsupported -> {
                    item(key = RELATED_SPOT_NOTICE_ITEM_KEY) {
                        BriefingNotice {
                            NoticeText(stringResource(Res.string.random_travel_related_spot_unsupported))
                        }
                    }
                }

                RelatedSpotsUiState.Error -> {
                    item(key = RELATED_SPOT_ERROR_ITEM_KEY) {
                        BriefingNotice {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                NoticeText(stringResource(Res.string.random_travel_related_spot_error))

                                TextButton(
                                    onClick = { onIntent(RandomTravelIntent.RetryRelatedSpots) },
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
                                NoticeText(stringResource(Res.string.random_travel_related_spot_empty))
                            }
                        }
                    } else {
                        items(
                            items = relatedSpotsUiState.relatedSpots,
                            key = { it.category },
                        ) { relatedSpot ->
                            RandomTravelRelatedSpotItem(
                                relatedSpot = relatedSpot,
                                onClick = {
                                    onRelatedSpotClick(destination.name, relatedSpot.category)
                                },
                            )
                        }
                    }
                }
            }
        }

        RandomTravelCtaSection(
            canStartTrip = uiState.canStartTrip,
            shouldShowSelectGuide = uiState.videos.isNotEmpty() && !uiState.canStartTrip,
            onStartTripClick = { onIntent(RandomTravelIntent.ClickStartTrip) },
            onRerollClick = { onIntent(RandomTravelIntent.ClickReroll) },
        )
    }
}

/**
 * 브리핑 본문의 섹션 제목. HTML 프로토타입의 `.sec .head`(왼쪽 제목 + 오른쪽 보조 텍스트)에 대응한다.
 */
@Composable
private fun BriefingSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = TuripTheme.typography.title1,
            color = TuripTheme.colors.gray04,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray03,
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.small)
                        .clip(TuripTheme.shape.chip)
                        .background(TuripTheme.colors.container)
                        .padding(
                            horizontal = TuripTheme.spacing.small,
                            vertical = TuripTheme.spacing.extraSmall,
                        ),
            )
        }
    }
}

/**
 * 섹션 하나가 목록 대신 짧은 안내(로딩/미지원/빈 상태/에러)를 보여줄 때 쓰는 자리.
 * [BriefingPlaceholder] 와 달리 높이를 고정하지 않아 한두 줄짜리 안내에 어울린다.
 */
@Composable
private fun BriefingNotice(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = TuripTheme.spacing.large),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

@Composable
private fun NoticeText(text: String) {
    Text(
        text = text,
        style = TuripTheme.typography.body2,
        color = TuripTheme.colors.gray03,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun BriefingPlaceholder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(BRIEFING_PLACEHOLDER_HEIGHT),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

@Composable
private fun BriefingErrorView(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(BRIEFING_PLACEHOLDER_HEIGHT),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.random_travel_briefing_error),
            style = TuripTheme.typography.body1,
            color = TuripTheme.colors.gray03,
            textAlign = TextAlign.Center,
        )

        TextButton(onClick = onRetryClick) {
            Text(
                text = stringResource(Res.string.retry),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun RandomTravelCtaSection(
    canStartTrip: Boolean,
    shouldShowSelectGuide: Boolean,
    onStartTripClick: () -> Unit,
    onRerollClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(TuripTheme.colors.white),
    ) {
        // 스크롤되는 목록과 고정 CTA를 구분하는 경계선
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(DIVIDER_HEIGHT)
                    .background(TuripTheme.colors.border),
        )

        Column(
            modifier =
                Modifier.padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.medium,
                ),
        ) {
            if (shouldShowSelectGuide) {
                Text(
                    text = stringResource(Res.string.random_travel_select_video_guide),
                    style = TuripTheme.typography.info1,
                    color = TuripTheme.colors.gray03,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = TuripTheme.spacing.small),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = onRerollClick,
                    shape = TuripTheme.shape.wideButton,
                    border =
                        BorderStroke(
                            width = REEL_BORDER_WIDTH,
                            color = TuripTheme.colors.border,
                        ),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = TuripTheme.colors.gray04,
                        ),
                    modifier = Modifier.height(CTA_HEIGHT),
                ) {
                    Text(
                        text = stringResource(Res.string.random_travel_reroll),
                        style = TuripTheme.typography.body2,
                    )
                }

                Button(
                    onClick = onStartTripClick,
                    enabled = canStartTrip,
                    shape = TuripTheme.shape.wideButton,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = TuripTheme.colors.primary,
                            contentColor = TuripTheme.colors.white,
                            disabledContainerColor = TuripTheme.colors.gray02,
                            disabledContentColor = TuripTheme.colors.white,
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(CTA_HEIGHT),
                ) {
                    Text(
                        text = stringResource(Res.string.random_travel_start_trip),
                        style = TuripTheme.typography.title2,
                    )
                }
            }
        }
    }
}

private val APP_BAR_ICON_SIZE = 36.dp
private val CTA_HEIGHT = 52.dp
private val BRIEFING_PLACEHOLDER_HEIGHT = 160.dp
private val REEL_BORDER_WIDTH = 1.5.dp
private val DIVIDER_HEIGHT = 1.dp
private val MORE_BUTTON_LOADING_SIZE = 16.dp
private const val CONFIRMED_REEL_SCALE: Float = 1.06f
private const val VIDEO_PREVIEW_COUNT: Int = 3
private const val TICKET_ITEM_KEY: String = "ticket"
private const val VIDEO_TITLE_ITEM_KEY: String = "video_title"
private const val VIDEO_MORE_ITEM_KEY: String = "video_more"
private const val RELATED_SPOT_TITLE_ITEM_KEY: String = "related_spot_title"
private const val RELATED_SPOT_LOADING_ITEM_KEY: String = "related_spot_loading"
private const val RELATED_SPOT_NOTICE_ITEM_KEY: String = "related_spot_notice"
private const val RELATED_SPOT_EMPTY_ITEM_KEY: String = "related_spot_empty"
private const val RELATED_SPOT_ERROR_ITEM_KEY: String = "related_spot_error"
private const val BRIEFING_LOADING_ITEM_KEY: String = "briefing_loading"
private const val BRIEFING_ERROR_ITEM_KEY: String = "briefing_error"
private const val BRIEFING_EMPTY_ITEM_KEY: String = "briefing_empty"

@Preview(showBackground = true, name = "슬롯 회전")
@Composable
private fun RandomTravelSpinningPreview() {
    TuripTheme {
        RandomTravelContent(
            uiState =
                RandomTravelState(
                    phase = RandomTravelPhase.Spinning,
                    reelNames = persistentListOf("서울", "부산", "제주", "강릉"),
                ),
            onIntent = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, name = "브리핑")
@Composable
private fun RandomTravelBriefingPreview() {
    TuripTheme {
        RandomTravelContent(
            uiState =
                RandomTravelState(
                    phase = RandomTravelPhase.Briefing,
                    destination =
                        RandomDestinationModel(
                            name = "서울",
                            imageUrl = "",
                            isDomestic = true,
                            videoCount = 12,
                        ),
                    relatedSpotsUiState =
                        RelatedSpotsUiState.Success(
                            persistentListOf(
                                RandomTravelRelatedSpotModel(
                                    category = "관광지",
                                    spots =
                                        persistentListOf(
                                            "종묘",
                                            "북촌한옥마을",
                                            "광장시장",
                                            "남산케이블카",
                                        ),
                                ),
                                RandomTravelRelatedSpotModel(
                                    category = "숙박",
                                    spots = persistentListOf("롯데호텔/서울점", "호텔국도", "로얄호텔서울"),
                                ),
                                RandomTravelRelatedSpotModel(
                                    category = "음식",
                                    spots = persistentListOf("토속촌삼계탕", "신마포회관"),
                                ),
                            ),
                        ),
                    isBriefingFetched = true,
                ),
            onIntent = {},
            onBackClick = {},
        )
    }
}
