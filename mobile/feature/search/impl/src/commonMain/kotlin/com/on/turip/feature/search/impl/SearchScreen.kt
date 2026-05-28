package com.on.turip.feature.search.impl

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.on.turip.feature.search.impl.component.SearchAppBar
import com.on.turip.feature.search.impl.component.SearchEmptyView
import com.on.turip.feature.search.impl.component.SearchHistoryList
import com.on.turip.feature.search.impl.component.SearchResultList
import com.on.turip.feature.search.impl.viewmodel.SearchEffect
import com.on.turip.feature.search.impl.viewmodel.SearchIntent
import com.on.turip.feature.search.impl.viewmodel.SearchResultState
import com.on.turip.feature.search.impl.viewmodel.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchScreen(
    keyword: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(parameters = { parametersOf(keyword) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SearchEffect.NavigateToLogin -> onNavigateToLogin()
                SearchEffect.ClearFocus -> focusManager.clearFocus()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        SearchAppBar(
            searchText = uiState.keyword,
            onSearchTextChanged = { viewModel.onIntent(SearchIntent.UpdateKeyword(it)) },
            onSearchAction = { viewModel.onIntent(SearchIntent.Search) },
            onClearClick = { viewModel.onIntent(SearchIntent.UpdateKeyword("")) },
            onBackClick = onNavigateBack,
            onFocusChanged = { hasFocus ->
                if (hasFocus) viewModel.onIntent(SearchIntent.ShowHistory)
            },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
        ) {
            when (val result = uiState.resultState) {
                SearchResultState.Idle -> Unit
                SearchResultState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                is SearchResultState.Empty -> SearchEmptyView(keyword = result.keyword)
                is SearchResultState.Success -> SearchResultList(
                    totalCount = result.totalCount,
                    contents = result.contents,
                    onItemClick = onNavigateToDetail,
                )
                SearchResultState.Error -> SearchEmptyView(keyword = uiState.keyword)
            }

            if (uiState.isHistoryVisible) {
                SearchHistoryList(
                    histories = uiState.searchHistory,
                    onHistoryClick = { viewModel.onIntent(SearchIntent.ClickHistory(it)) },
                    onDeleteClick = { viewModel.onIntent(SearchIntent.DeleteHistory(it)) },
                )
            }
        }
    }
}
