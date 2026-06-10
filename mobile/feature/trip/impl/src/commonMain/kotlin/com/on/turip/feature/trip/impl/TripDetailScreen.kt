package com.on.turip.feature.trip.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.trip_detail_snackbar_bookmark_remove
import com.on.turip.core.designsystem.generated.resources.trip_detail_snackbar_bookmark_save
import com.on.turip.core.designsystem.generated.resources.trip_detail_turip_selection_updated
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.trip.impl.component.ContentBookmarkButton
import com.on.turip.feature.trip.impl.component.ContentInformation
import com.on.turip.feature.trip.impl.component.ContentVideo
import com.on.turip.feature.trip.impl.component.CreatorInformation
import com.on.turip.feature.trip.impl.component.PlaceItem
import com.on.turip.feature.trip.impl.component.TripDetailAppBar
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.model.PlaceModel
import com.on.turip.feature.trip.impl.model.TripDetailInfoModel
import com.on.turip.feature.trip.impl.model.TripDurationModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripDetailScreen(
    contentId: Long,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToTuripDetail: (turipId: Long) -> Unit,
    navigateToMap: (mapModel: MapModel) -> Unit,
    navigateToWebViewUrl: (url: String) -> Unit,
    navigateToShareTuripByText: (turipShareModel: TuripShareModel) -> Unit,
    navigateToShareTuripInvitationLink: (invitationLink: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripDetailViewModel = koinViewModel(),
) {
    val uiState: TripDetailUiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current
    val listState = rememberLazyListState()

    LaunchedEffect(contentId) {
        viewModel.initContentId(contentId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: TripDetailUiEffect ->
            when (uiEffect) {
                is TripDetailUiEffect.ShowBookmarkStatus -> {
                    val messageResource =
                        if (uiEffect.isBookmarked) {
                            Res.string.trip_detail_snackbar_bookmark_save
                        } else {
                            Res.string.trip_detail_snackbar_bookmark_remove
                        }
                    snackbarDelegate.showSnackbar(
                        message = getString(messageResource),
                        actionLabel = getString(Res.string.all_close_description),
                    )
                }

                TripDetailUiEffect.NavigateToLogin -> {
                    navigateToLogin()
                }

                is TripDetailUiEffect.ShowUpdatedTuripSelectionByPlace -> {
                    snackbarDelegate.showSnackbar(
                        message =
                            getString(Res.string.trip_detail_turip_selection_updated)
                                .formatResource(uiEffect.placeName),
                        actionLabel = getString(Res.string.all_close_description),
                    )
                }

                is TripDetailUiEffect.ShowError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
                        message = getString(uiModel.titleRes),
                        actionLabel = getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is TripDetailUiEffect.ShowAddedError -> {
                    val uiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarDelegate.showSnackbar(
                        message = getString(uiModel.titleRes),
                        actionLabel = getString(uiModel.retryTextRes),
                        duration = SnackbarDuration.Long,
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }

                is TripDetailUiEffect.TuripAdded -> {
                    viewModel.dismissAddTuripBottomSheet()
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.systemBars)
                    .background(TuripTheme.colors.primary),
        )
        TripDetailAppBar(
            isError = uiState.errorUiState != ErrorUiState.None,
            isLoading = uiState.isLoading,
            isBookmarked = uiState.isBookmarked,
            onBackClick = navigateToBack,
            onBookmarkClick = viewModel::updateBookmark,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.errorUiState != ErrorUiState.None -> {
                    ErrorScreen(
                        errorUiState = uiState.errorUiState,
                        onRetryClick = viewModel::loadTripDetails,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier =
                            Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                        color = TuripTheme.colors.primary,
                    )
                }

                else -> {
                    TripDetailScreenContent(
                        uiState = uiState,
                        listState = listState,
                        onTimeLineClick = viewModel::updateVideoPlaybackSecond,
                        onMapClick = navigateToMap,
                        onTuripPlaceClick = viewModel::selectPlace,
                        onBookmarkClick = viewModel::updateBookmark,
                        onErrorVideoClick = { navigateToWebViewUrl(uiState.tripDetailInfo.videoLink) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    navigateToTuripDetail
    navigateToShareTuripByText
    navigateToShareTuripInvitationLink
}

@Composable
private fun TripDetailScreenContent(
    uiState: TripDetailUiState,
    listState: LazyListState,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClick: (id: Long, placeName: String) -> Unit,
    onBookmarkClick: () -> Unit,
    onErrorVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier =
            modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .background(TuripTheme.colors.white),
    ) {
        item {
            CreatorInformation(
                thumbnailUrl = uiState.tripDetailInfo.creatorThumbnail,
                name = uiState.tripDetailInfo.creatorName,
            )
        }

        stickyHeader {
            ContentVideo(onErrorClick = onErrorVideoClick)
        }

        item {
            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraLarge))
            ContentInformation(information = uiState.tripDetailInfo)
            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))
        }

        items(
            items = uiState.places,
            key = { "${it.id}_${it.timeLine}" },
        ) { place ->
            PlaceItem(
                placeModel = place,
                onTimeLineClick = onTimeLineClick,
                onMapClick = onMapClick,
                onTuripPlaceClick = onTuripPlaceClick,
                modifier =
                    Modifier
                        .padding(
                            start = TuripTheme.spacing.extraLarge,
                            end = TuripTheme.spacing.extraLarge,
                            bottom = TuripTheme.spacing.small,
                        ).fillMaxWidth(),
            )
        }

        item {
            ContentBookmarkButton(
                isBookmarked = uiState.isBookmarked,
                onClick = onBookmarkClick,
                modifier =
                    Modifier
                        .padding(
                            horizontal = TuripTheme.spacing.extraLarge,
                            vertical = TuripTheme.spacing.medium,
                        ).fillMaxWidth(),
            )
        }
    }
}

private val sampleTripDetailInfo =
    TripDetailInfoModel(
        creatorName = "여행하는 튜립팀",
        creatorThumbnail = "",
        city = "서울",
        videoLink = "",
        contentTitle = "서울 당일치기 여행 코스",
        uploadedDate = "2026-02-23",
        placeTotalCount = 3,
        duration = TripDurationModel(0, 1),
    )

private val samplePlaces =
    persistentListOf(
        PlaceModel(
            id = 1L,
            name = "안국역",
            isTuripPlace = true,
            category = "역",
            mapLink = "kakao.com/123",
            timeLine = "01:03",
        ),
        PlaceModel(
            id = 2L,
            name = "북촌한옥마을",
            isTuripPlace = false,
            category = "명소",
            mapLink = "google.com/maps",
            timeLine = "03:12",
        ),
    )

@Preview(showBackground = true)
@Composable
private fun TripContentScreenPreview() {
    TuripTheme {
        TripDetailScreenContent(
            uiState =
                TripDetailUiState(
                    isLoading = false,
                    errorUiState = ErrorUiState.None,
                    tripDetailInfo = sampleTripDetailInfo,
                    places = samplePlaces,
                    isBookmarked = true,
                    selectedPlaceModel = null,
                ),
            listState = rememberLazyListState(),
            onTimeLineClick = {},
            onMapClick = {},
            onTuripPlaceClick = { _, _ -> },
            onBookmarkClick = {},
            onErrorVideoClick = {},
            modifier = Modifier.background(color = TuripTheme.colors.white),
        )
    }
}
