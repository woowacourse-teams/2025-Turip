package com.on.turip.feature.search.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.on.turip.feature.search.impl.component.RegionResultAppBar
import com.on.turip.feature.search.impl.component.SearchResultList
import com.on.turip.feature.search.impl.viewmodel.RegionResultIntent
import com.on.turip.feature.search.impl.viewmodel.RegionResultViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RegionResultScreen(
    regionCategoryName: String,
    onBackClick: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit,
    viewModel: RegionResultViewModel = koinViewModel(parameters = { parametersOf(regionCategoryName) }),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        RegionResultAppBar(
            title = regionCategoryName,
            onBackClick = onBackClick,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                uiState.isError -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> SearchResultList(
                    totalCount = uiState.totalCount,
                    contents = uiState.contents,
                    onItemClick = onNavigateToDetail,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
