package com.on.turip.feature.search.impl.regionresult

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.feature.search.impl.component.SearchResultList
import com.on.turip.feature.search.impl.component.SearchResultListSkeleton
import com.on.turip.feature.search.impl.regionresult.component.RegionResultAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegionResultScreen(
    regionCategoryName: String,
    onBackClick: () -> Unit,
    onNavigateToTripDetail: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionResultViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                RegionResultUiEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initRegionCategoryName(regionCategoryName)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = TuripTheme.colors.white,
            modifier = Modifier.statusBarsPadding(),
        ) {
            RegionResultAppBar(
                title = regionCategoryName,
                onBackClick = onBackClick,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                RegionResultUiState.Loading -> {
                    SearchResultListSkeleton()
                }

                RegionResultUiState.Empty -> {
                    // Original Android currently has no visible empty-region UI.
                }

                is RegionResultUiState.Success -> {
                    SearchResultList(
                        totalCount = state.totalCount,
                        videos = state.videos,
                        onItemClick = onNavigateToTripDetail,
                    )
                }

                is RegionResultUiState.Error -> {
                    ErrorScreen(
                        errorUiState = state.errorUiState,
                        onRetryClick = { viewModel.loadContentsFromRegion(regionCategoryName) },
                    )
                }
            }
        }
    }
}
