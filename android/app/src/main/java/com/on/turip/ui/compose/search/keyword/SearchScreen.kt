package com.on.turip.ui.compose.search.keyword

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.keyword.component.SearchAppBar
import com.on.turip.ui.compose.search.keyword.component.SearchEmptyView
import com.on.turip.ui.compose.search.keyword.component.SearchHistoryList
import com.on.turip.ui.compose.search.component.SearchResultList
import com.on.turip.ui.search.keywordresult.SearchUiEffect
import com.on.turip.ui.search.keywordresult.SearchUiState
import com.on.turip.ui.search.keywordresult.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEffect by viewModel.uiEffect.collectAsStateWithLifecycle(initialValue = null)
    val searchingWord by viewModel.searchingWord.observeAsState("")
    val searchHistory by viewModel.searchHistory.observeAsState(emptyList())

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isHistoryVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiEffect) {
        when (uiEffect) {
            SearchUiEffect.NavigateToLogin -> onNavigateToLogin()
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            SearchAppBar(
                searchText = searchingWord,
                onSearchTextChanged = { viewModel.updateSearchingWord(it) },
                onSearchAction = {
                    if (searchingWord.isNotBlank()) {
                        viewModel.loadByKeyword()
                        viewModel.createSearchHistory()
                        isHistoryVisible = false
                        focusManager.clearFocus()
                    }
                },
                onClearClick = { viewModel.updateSearchingWord("") },
                onBackClick = onNavigateBack,
                onFocusChanged = { hasFocus ->
                    if (hasFocus && uiState !is SearchUiState.Error) {
                        isHistoryVisible = true
                    }
                },
            )
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
