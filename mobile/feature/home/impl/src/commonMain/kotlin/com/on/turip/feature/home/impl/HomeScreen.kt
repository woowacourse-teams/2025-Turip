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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.home_top_title
import com.on.turip.core.designsystem.generated.resources.home_users_like_content_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.home.impl.component.HomeAppBar
import com.on.turip.feature.home.impl.component.RegionList
import com.on.turip.feature.home.impl.component.RegionTypeButtons
import com.on.turip.feature.home.impl.component.SearchTextField
import com.on.turip.feature.home.impl.component.UsersLikeList
import com.on.turip.feature.home.impl.model.UsersLikeContentModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onSearchClick: (keyword: String) -> Unit,
    onRegionClick: (regionName: String) -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState: HomeUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { uiEffect: HomeUiEffect ->
            when (uiEffect) {
                HomeUiEffect.NavigateToLogin -> onNavigateToLoginScreen()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .background(TuripTheme.colors.white),
        )
        HomeAppBar()
        HomeScreenContent(
            uiState = uiState,
            onSearchClick = onSearchClick,
            onRetryLoadContents = viewModel::loadContents,
            onContentClick = { usersLikeContent: UsersLikeContentModel ->
                onContentClick(usersLikeContent.content.id)
            },
            onRegionClick = onRegionClick,
            onDomesticClick = { viewModel.updateDomesticSelected(it) },
        )
    }
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onRetryLoadContents: () -> Unit,
    onSearchClick: (keyword: String) -> Unit,
    onContentClick: (usersLikeContentModel: UsersLikeContentModel) -> Unit,
    onRegionClick: (regionName: String) -> Unit,
    onDomesticClick: (isDomestic: Boolean) -> Unit,
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
                    text = stringResource(Res.string.home_top_title),
                    color = TuripTheme.colors.gray04,
                    style = TuripTheme.typography.title1,
                )

                SearchTextField(
                    keyword = keyword,
                    onKeywordChange = { newKeyword -> keyword = newKeyword },
                    onSearch = { searchKeyword ->
                        onSearchClick(searchKeyword)
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    },
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .padding(top = TuripTheme.spacing.extraSmall),
                )

                Text(
                    text = stringResource(Res.string.home_users_like_content_title),
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
                    modifier = Modifier.padding(bottom = TuripTheme.spacing.large),
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
                onRetryLoadContents = {},
                onContentClick = {},
                onRegionClick = {},
                onDomesticClick = {},
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
                onRetryLoadContents = {},
                onContentClick = {},
                onRegionClick = {},
                onDomesticClick = {},
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
                onRetryLoadContents = {},
                onContentClick = {},
                onRegionClick = {},
                onDomesticClick = {},
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
                onRetryLoadContents = {},
                onContentClick = {},
                onRegionClick = {},
                onDomesticClick = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
