package com.on.turip.feature.popularregion.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.popularregion.impl.component.HeatLegendCard
import com.on.turip.feature.popularregion.impl.component.KoreaHeatMap
import com.on.turip.feature.popularregion.impl.component.PopularRegionAppBar
import com.on.turip.feature.popularregion.impl.component.PopularRegionMapBadge
import com.on.turip.feature.popularregion.impl.component.PopularRegionMapHint
import com.on.turip.feature.popularregion.impl.component.PopularRegionSheetContent
import com.on.turip.feature.popularregion.impl.component.baseMonthText
import com.on.turip.feature.popularregion.impl.model.PopularRegionModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PopularRegionScreen(
    onBackClick: () -> Unit,
    onRegionContentsClick: (regionCategoryName: String) -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PopularRegionViewModel = koinViewModel(),
) {
    val uiState: PopularRegionState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: PopularRegionEffect ->
            when (effect) {
                is PopularRegionEffect.NavigateToRegionResult ->
                    onRegionContentsClick(effect.regionCategoryName)

                is PopularRegionEffect.NavigateToTripDetail ->
                    onContentClick(effect.contentId)

                PopularRegionEffect.NavigateToLogin -> onNavigateToLoginScreen()
            }
        }
    }

    PopularRegionContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PopularRegionContent(
    uiState: PopularRegionState,
    onIntent: (PopularRegionIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState: BottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(
            bottomSheetState =
                rememberStandardBottomSheetState(
                    initialValue = SheetValue.Hidden,
                    skipHiddenState = false,
                ),
        )

    // 시트 위치는 선택된 지역을 따라간다. 시트를 여닫는 코드는 여기 한 곳뿐이다.
    LaunchedEffect(uiState.selectedRegionCode) {
        if (uiState.selectedRegionCode == null) {
            scaffoldState.bottomSheetState.hide()
        } else {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    // 시트 본문은 내비게이션 바를 피해 그려지므로, 기기마다 다른 그 높이만큼
    // peek 높이도 같이 늘려야 어느 기기에서든 같은 분량이 보인다.
    val navigationBarHeight: Dp =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val sheetPeekHeight: Dp = SHEET_PEEK_HEIGHT + navigationBarHeight

    // 지도는 늘 화면 전체에 그리고, 시트가 덮는 만큼만 위로 밀어 맞춘다.
    // 시트가 없을 때 그 높이가 빈 칸으로 남지 않게 하려는 것이다.
    val mapBottomInset: Dp by animateDpAsState(
        targetValue = if (uiState.shouldShowSheet) sheetPeekHeight else 0.dp,
        label = "mapBottomInset",
    )

    // 사용자가 시트를 아래로 끌어 내린 경우에도 선택 상태를 함께 비운다.
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            onIntent(PopularRegionIntent.DismissSheet)
        }
    }

    val baseMonthLabel: String =
        baseMonthText(year = uiState.baseYear, monthOfYear = uiState.baseMonthOfYear)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                // 앱이 edge-to-edge 라 화면이 시스템 바 영역까지 받는다.
                // 위쪽은 앱바가 상태 바에 겹치지 않게 밀어낸다.
                // 아래쪽은 바다가 내비게이션 바 뒤까지 깔리는 편이 자연스러워 여기서 밀지 않고,
                // 시트 본문과 지도 위 안내가 각자 피한다.
                .statusBarsPadding(),
    ) {
        PopularRegionAppBar(
            baseMonthText = baseMonthLabel,
            onBackClick = onBackClick,
        )

        if (uiState.errorUiState != ErrorUiState.None) {
            ErrorScreen(
                errorUiState = uiState.errorUiState,
                onRetryClick = { onIntent(PopularRegionIntent.RetryLoad) },
            )
            return@Column
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetPeekHeight,
            sheetContainerColor = TuripTheme.colors.white,
            sheetShape = TuripTheme.shape.bottomSheetRounded,
            containerColor = TuripTheme.colors.white,
            sheetContent = {
                val region: PopularRegionModel? = uiState.selectedRegion
                if (region != null) {
                    PopularRegionSheetContent(
                        region = region,
                        isTopRegion = region.code == uiState.topRegionCode,
                        baseMonthText = baseMonthLabel,
                        contentsUiState = uiState.contentsUiState,
                        onRelatedContentsClick = {
                            onIntent(PopularRegionIntent.ClickRelatedContents)
                        },
                        onContentClick = { onIntent(PopularRegionIntent.ClickContent(it)) },
                        onRetryContentsClick = { onIntent(PopularRegionIntent.RetryContents) },
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            // innerPadding 을 쓰지 않는다. 그걸 적용하면 시트가 닫혀 있을 때도
            // 시트 높이만큼 빈 칸이 남는다. 대신 지도에 bottomInset 을 넘겨 맞춘다.
            Box(modifier = Modifier.fillMaxSize()) {
                KoreaHeatMap(
                    heatPoints = uiState.heatPoints,
                    selectedRegionCode = uiState.selectedRegionCode,
                    onRegionClick = { onIntent(PopularRegionIntent.SelectRegion(it)) },
                    onEmptyClick = { onIntent(PopularRegionIntent.DismissSheet) },
                    modifier = Modifier.fillMaxSize(),
                    bottomInset = mapBottomInset,
                )

                PopularRegionMapBadge(
                    baseMonthText = baseMonthLabel,
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(TuripTheme.spacing.large),
                )

                HeatLegendCard(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(TuripTheme.spacing.large),
                )

                MapHintOverlay(
                    isVisible = uiState.shouldShowMapHint,
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = TuripTheme.spacing.extraHuge),
                )
            }
        }
    }
}

/**
 * 안내 문구를 감싸는 얇은 래퍼.
 *
 * 화면 본문은 Column 안이라 [AnimatedVisibility] 가 `ColumnScope` 오버로드로 잡힌다.
 * 스코프 밖으로 빼서 최상위 오버로드를 쓰게 한다.
 */
@Composable
private fun MapHintOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        PopularRegionMapHint()
    }
}

/** 내비게이션 바 높이를 뺀, 시트가 기본으로 보여줄 내용 높이 */
private val SHEET_PEEK_HEIGHT = 360.dp

@Preview(showBackground = true, name = "지역 선택 전")
@Composable
private fun PopularRegionIdlePreview() {
    TuripTheme {
        PopularRegionContent(
            uiState = PopularRegionState(isLoading = false),
            onIntent = {},
            onBackClick = {},
        )
    }
}
