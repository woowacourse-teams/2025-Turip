package com.on.turip.feature.home.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.home.impl.component.HomeAppBar
import com.on.turip.feature.home.impl.component.RegionList
import com.on.turip.feature.home.impl.component.RegionTypeButtons
import com.on.turip.feature.home.impl.component.SearchTextField
import com.on.turip.feature.home.impl.component.UsersLikeList
import com.on.turip.feature.home.impl.viewmodel.HomeEffect
import com.on.turip.feature.home.impl.viewmodel.HomeIntent
import com.on.turip.feature.home.impl.viewmodel.HomeState
import com.on.turip.feature.home.impl.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onSearchClick: (keyword: String) -> Unit,
    onRegionClick: (regionName: String) -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .background(TuripTheme.colors.white),
        )
        HomeAppBar()
        HomeContent(
            uiState = uiState,
            onSearchClick = onSearchClick,
            onRetry = { viewModel.onIntent(HomeIntent.LoadContents) },
            onContentClick = onContentClick,
            onRegionClick = onRegionClick,
            onDomesticClick = { viewModel.onIntent(HomeIntent.SelectDomestic(it)) },
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeState,
    onSearchClick: (keyword: String) -> Unit,
    onRetry: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onRegionClick: (regionName: String) -> Unit,
    onDomesticClick: (isDomestic: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    when {
        uiState.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(60.dp), color = TuripTheme.colors.black)
            }
        }

        uiState.isError -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "오류가 발생했어요", style = TuripTheme.typography.title2)
                    Text(
                        text = "다시 시도",
                        style = TuripTheme.typography.title3,
                        color = TuripTheme.colors.primary,
                        modifier = Modifier
                            .padding(top = TuripTheme.spacing.medium)
                            .pointerInput(Unit) { detectTapGestures { onRetry() } },
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    }
                    .padding(horizontal = TuripTheme.spacing.extraLarge)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
            ) {
                Text(text = "어디로 떠나볼까요?", color = TuripTheme.colors.gray04, style = TuripTheme.typography.title1)

                SearchTextField(
                    keyword = keyword,
                    onKeywordChange = { keyword = it },
                    onSearch = { kw ->
                        onSearchClick(kw)
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = TuripTheme.spacing.extraSmall),
                )

                Text(
                    text = "유저들이 좋아하는 콘텐츠",
                    modifier = Modifier.padding(top = TuripTheme.spacing.medium),
                    color = TuripTheme.colors.gray04,
                    style = TuripTheme.typography.title1,
                )

                UsersLikeList(
                    usersLikeContents = uiState.usersLikeContents,
                    onContentClick = onContentClick,
                )

                RegionTypeButtons(
                    onDomesticClick = onDomesticClick,
                    isSelectedDomestic = uiState.isDomesticSelected,
                )

                RegionList(
                    regions = uiState.regionCategories,
                    onRegionClick = onRegionClick,
                    modifier = Modifier.padding(bottom = TuripTheme.spacing.large),
                )
            }
        }
    }
}
