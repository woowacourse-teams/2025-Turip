package com.on.turip.ui.compose.search.keyword

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.component.SearchResultList
import com.on.turip.ui.compose.search.keyword.component.SearchAppBar
import com.on.turip.ui.compose.search.keyword.component.SearchEmptyView
import com.on.turip.ui.compose.search.keyword.component.SearchHistoryList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SearchScreen(
    keyword: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchingWord by viewModel.searchingWord.observeAsState("")
    val searchHistory by viewModel.searchHistory.observeAsState(persistentListOf())

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
        viewModel.initKeywordIfNeeded(keyword)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                SearchUiEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
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
        },
        containerColor = TuripTheme.colors.white,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .addFocusCleaner(focusManager),
        ) {
            when (val state = uiState) {
                is SearchUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
