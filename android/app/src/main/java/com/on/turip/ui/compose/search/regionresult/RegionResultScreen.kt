package com.on.turip.ui.compose.search.regionresult

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.component.SearchResultList
import com.on.turip.ui.compose.search.regionresult.component.RegionResultAppBar
import com.on.turip.ui.search.regionresult.RegionResultUiEffect
import com.on.turip.ui.search.regionresult.RegionResultUiState
import com.on.turip.ui.search.regionresult.RegionResultViewModel

@Composable
fun RegionResultScreen(
    onBackClick: () -> Unit,
    onItemClick: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegionResultViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEffect by viewModel.uiEffect.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    RegionResultUiEffect.NavigateToLogin -> onNavigateToLogin()
                }
            }
        }
    }
    LaunchedEffect(uiEffect) {
        when (uiEffect) {
            RegionResultUiEffect.NavigateToLogin -> onNavigateToLogin()
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = TuripTheme.colors.white,
                modifier = Modifier.statusBarsPadding(),
            ) {
                RegionResultAppBar(
                    title = viewModel.regionCategoryName,
                    onBackClick = onBackClick,
                )
            }
        },
        containerColor = TuripTheme.colors.white,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when (val state = uiState) {
                is RegionResultUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RegionResultUiState.Empty -> {
                    // TODO: 없는 경우는 없음 (현재)
                }

                is RegionResultUiState.Success -> {
                    SearchResultList(
                        totalCount = state.totalCount,
                        videos = state.videos,
                        onItemClick = onItemClick,
                    )
                }

                is RegionResultUiState.Error -> {
                    ErrorScreen(
                        errorUiState = state.errorUiState,
                        onRetryClick = { viewModel.loadContentsFromRegion() },
                    )
                }
            }
        }
    }
}
