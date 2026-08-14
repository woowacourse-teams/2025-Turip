package com.on.turip.feature.search.impl.keyword

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.feature.search.impl.component.SearchResultList
import com.on.turip.feature.search.impl.component.SearchResultListSkeleton
import com.on.turip.feature.search.impl.keyword.component.SearchAppBar
import com.on.turip.feature.search.impl.keyword.component.SearchEmptyView
import com.on.turip.feature.search.impl.keyword.component.SearchHistoryList
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    keyword: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchingWord by viewModel.searchingWord.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    val focusManager = LocalFocusManager.current
    var isHistoryVisible by remember { mutableStateOf(false) }

    val onSearchAction = {
        if (searchingWord.isNotBlank()) {
            viewModel.loadByKeyword()
            viewModel.createSearchHistory()
            isHistoryVisible = false
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initKeyword(keyword)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                SearchUiEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(top = TuripTheme.spacing.medium)
                .navigationBarsPadding(),
    ) {
        Surface(
            color = TuripTheme.colors.white,
            modifier = Modifier.statusBarsPadding(),
        ) {
            SearchAppBar(
                searchText = searchingWord,
                onSearchTextChanged = { viewModel.updateSearchingWord(it) },
                onSearchAction = onSearchAction,
                onClearClick = { viewModel.updateSearchingWord("") },
                onBackClick = onNavigateBack,
                onFocusChanged = { hasFocus ->
                    if (hasFocus && uiState !is SearchUiState.Error) {
                        isHistoryVisible = true
                    }
                },
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .addFocusCleaner(focusManager),
        ) {
            when (val state = uiState) {
                SearchUiState.Loading -> {
                    SearchResultListSkeleton()
                }

                is SearchUiState.Empty -> {
                    SearchEmptyView(state.keyword)
                }

                is SearchUiState.Success -> {
                    SearchResultList(
                        totalCount = state.totalCount,
                        videos = state.videos,
                        onItemClick = onNavigateToDetail,
                    )
                }

                is SearchUiState.Error -> {
                    ErrorScreen(
                        errorUiState = state.errorUiState,
                        onRetryClick = { viewModel.loadByKeyword() },
                    )
                }
            }

            if (isHistoryVisible) {
                SearchHistoryList(
                    histories = searchHistory,
                    onHistoryClick = { keyword ->
                        viewModel.updateSearchingWord(keyword)
                        viewModel.loadByKeyword()
                        viewModel.createSearchHistory()
                        isHistoryVisible = false
                        focusManager.clearFocus()
                    },
                    onDeleteClick = { viewModel.deleteSearchHistory(it) },
                )
            }
        }
    }
}

private fun Modifier.addFocusCleaner(focusManager: FocusManager): Modifier =
    pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }
