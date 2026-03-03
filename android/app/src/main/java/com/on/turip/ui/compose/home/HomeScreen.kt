package com.on.turip.ui.compose.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.home.component.HomeAppBar
import com.on.turip.ui.compose.home.component.RegionList
import com.on.turip.ui.compose.home.component.RegionTypeButtons
import com.on.turip.ui.compose.home.component.SearchTextField
import com.on.turip.ui.compose.home.component.UsersLikeList
import com.on.turip.ui.main.home.model.UsersLikeContentModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onSearchClick: (String) -> Unit,
    onRegionClick: (String) -> Unit,
    onContentClick: (Long) -> Unit,
    navigateToLoginScreen: () -> Unit,
    navigateToMyPage: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState: HomeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { uiEffect: HomeUiEffect ->
            when (uiEffect) {
                HomeUiEffect.NavigateToLogin -> navigateToLoginScreen()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(),
        topBar = { HomeAppBar() },
    ) { innerPadding ->
        HomeScreenContent(
            uiState = uiState,
            onSearchClick = onSearchClick,
            onRetryLoadContents = viewModel::loadContents,
            onContentClick = { usersLikeContent: UsersLikeContentModel ->
                onContentClick(usersLikeContent.content.id)
            },
            onRegionClick = onRegionClick,
            onDomesticClick = { viewModel.updateDomesticSelected(it) },
            onNavigateToMyPage = navigateToMyPage,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onRetryLoadContents: () -> Unit,
    onSearchClick: (String) -> Unit,
    onContentClick: (UsersLikeContentModel) -> Unit,
    onRegionClick: (String) -> Unit,
    onDomesticClick: (Boolean) -> Unit,
    onNavigateToMyPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var keyword: String by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = TuripTheme.colors.black,
            )
        }
    }
    when {
        uiState.errorUiState != ErrorUiState.None -> {
            ErrorScreen(
                errorUiState = uiState.errorUiState,
                onRetryClick = onRetryLoadContents,
                modifier = modifier.fillMaxSize(),
            )
        }

        else -> {
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            })
                        }.padding(horizontal = TuripTheme.spacing.extraLarge)
                        .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
            ) {
                Text(
                    text = stringResource(R.string.home_top_title),
                    color = TuripTheme.colors.gray04,
                    style = TuripTheme.typography.title1,
                )

                SearchTextField(
                    keyword = keyword,
                    onKeywordChange = { newKeyword -> keyword = newKeyword },
                    onSearch = { keyword ->
                        onSearchClick(keyword)
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    },
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .padding(top = TuripTheme.spacing.extraSmall),
                )

                Text(
                    text = stringResource(R.string.home_users_like_content_title),
                    modifier = Modifier.padding(top = TuripTheme.spacing.medium),
                    color = TuripTheme.colors.gray04,
                    style = TuripTheme.typography.title1,
                )

                UsersLikeList(
                    usersLikeContents = uiState.usersLikeContents,
                    onContentClick = onContentClick,
                )

                RegionTypeButtons(
                    onDomesticClick = { onDomesticClick(it) },
                    isSelectedDomestic = uiState.isDomesticSelected,
                )

                RegionList(
                    regions = uiState.regionCategories,
                    onRegionClick = onRegionClick,
                )

                Text(
                    text = "임시 마이페이지 확인용 텍스트",
                    modifier =
                        Modifier
                            .padding(top = TuripTheme.spacing.large)
                            .clickable(onClick = onNavigateToMyPage),
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "로딩")
@Composable
private fun HomeLoadingPreview() {
    val uiState = HomeUiState.Idle
    TuripTheme {
        Scaffold(topBar = { HomeAppBar() }) { innerPadding ->
            HomeScreenContent(
                uiState = uiState.copy(isLoading = true),
                onSearchClick = {},
                onRetryLoadContents = { },
                onContentClick = { },
                onRegionClick = { },
                onDomesticClick = { },
                onNavigateToMyPage = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Preview(showBackground = true, name = "성공")
@Composable
private fun HomeSuccessPreview() {
    val uiState =
        HomeUiState(
            isLoading = false,
            regionCategories = emptyList(),
            isDomesticSelected = true,
            usersLikeContents = emptyList(),
            errorUiState = ErrorUiState.None,
        )
    TuripTheme {
        Scaffold(topBar = { HomeAppBar() }) { innerPadding ->
            HomeScreenContent(
                uiState = uiState,
                onSearchClick = {},
                onRetryLoadContents = { },
                onContentClick = { },
                onRegionClick = { },
                onDomesticClick = { },
                onNavigateToMyPage = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Preview(showBackground = true, name = "서버 에러 발생")
@Composable
private fun HomeServerErrorPreview() {
    val uiState = HomeUiState.Idle
    TuripTheme {
        Scaffold(topBar = { HomeAppBar() }) { innerPadding ->
            HomeScreenContent(
                uiState = uiState.copy(isLoading = false, errorUiState = ErrorUiState.Server),
                onSearchClick = {},
                onRetryLoadContents = { },
                onContentClick = { },
                onRegionClick = { },
                onDomesticClick = { },
                onNavigateToMyPage = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Preview(showBackground = true, name = "네트워크 에러 발생")
@Composable
private fun HomeNetworkErrorPreview() {
    val uiState = HomeUiState.Idle
    TuripTheme {
        Scaffold(topBar = { HomeAppBar() }) { innerPadding ->
            HomeScreenContent(
                uiState = uiState.copy(isLoading = false, errorUiState = ErrorUiState.Network),
                onSearchClick = {},
                onRetryLoadContents = { },
                onContentClick = { },
                onRegionClick = { },
                onDomesticClick = { },
                onNavigateToMyPage = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
