package com.on.turip.ui.compose.search.regionresult

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.keyword.component.SearchResultList
import com.on.turip.ui.compose.search.regionresult.component.RegionResultAppBar
import com.on.turip.ui.search.regionresult.RegionResultUiEffect
import com.on.turip.ui.search.regionresult.RegionResultUiState

@Composable
fun RegionResultScreen(
    regionName: String,
    uiState: RegionResultUiState,
    uiEffect: RegionResultUiEffect?,
    onBackClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    onRetryClick: () -> Unit,
) {
    LaunchedEffect(uiEffect) {
        when (uiEffect) {
            RegionResultUiEffect.NavigateToLogin -> onNavigateToLogin()
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            RegionResultAppBar(
                title = regionName,
                onBackClick = onBackClick,
            )
        },
        containerColor = TuripTheme.colors.white,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when (uiState) {
                is RegionResultUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RegionResultUiState.Empty -> {
                    // TODO: 없는 경우는 없음 (현재)
                }

                is RegionResultUiState.Success -> {
                    SearchResultList(
                        totalCount = uiState.totalCount,
                        videos = uiState.videos,
                        onItemClick = onItemClick,
                    )
                }

                is RegionResultUiState.Error -> {
                    ErrorScreen(uiState.errorUiState, onRetryClick)
                }
            }
        }
    }
}
