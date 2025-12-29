package com.on.turip.ui.compose.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.data.common.ErrorUiState
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.home.component.RegionList
import com.on.turip.ui.compose.home.component.RegionTypeButtons
import com.on.turip.ui.compose.home.component.SearchTextField
import com.on.turip.ui.compose.home.component.UsersLikeList
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.compose.theme.TuripTypography
import com.on.turip.ui.main.home.model.UsersLikeContentModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onSearchClick: (String) -> Unit,
    onRegionClick: (String) -> Unit,
    onContentClick: (UsersLikeContentModel) -> Unit,
    navigateToLoginScreen: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState: HomeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.commonUiEffect.collectLatest { uiEffect: CommonUiEffect ->
            when (uiEffect) {
                CommonUiEffect.NavigateToLogin -> navigateToLoginScreen()
            }
        }
    }

    HomeScreenLayout(
        uiState = uiState,
        onRetryLoadContents = viewModel::loadContents,
        onSearchClick = onSearchClick,
        onContentClick = onContentClick,
        onRegionClick = onRegionClick,
        onDomesticClick = { viewModel.updateDomesticSelected(it) },
    )
}

@Composable
private fun HomeScreenLayout(
    uiState: HomeUiState,
    onRetryLoadContents: () -> Unit,
    onSearchClick: (String) -> Unit,
    onContentClick: (UsersLikeContentModel) -> Unit,
    onRegionClick: (String) -> Unit,
    onDomesticClick: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TuripAppBar(canBack = false)
        },
    ) { innerPadding ->
        HomeScreenContent(
            uiState = uiState,
            onSearchClick = onSearchClick,
            onRetryLoadContents = onRetryLoadContents,
            onContentClick = onContentClick,
            onRegionClick = onRegionClick,
            onDomesticClick = onDomesticClick,
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
    modifier: Modifier = Modifier,
) {
    var keyword: String by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    when {
        uiState.errorUiState != ErrorUiState.None -> {
            ErrorScreen(
                errorUiState = uiState.errorUiState,
                onRetryClick = onRetryLoadContents,
                modifier = modifier.fillMaxSize(),
            )
        }

        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(60.dp), color = Color.Black)
            }
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
                        }.padding(horizontal = 20.dp)
                        .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_top_title),
                    color = colorResource(R.color.gray_400_2b2b2b),
                    style = TuripTypography.titleLarge,
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
                            .padding(top = 4.dp, end = 20.dp),
                )

                Text(
                    text = stringResource(R.string.home_users_like_content_title),
                    modifier = Modifier.padding(top = 14.dp),
                    color = colorResource(R.color.gray_400_2b2b2b),
                    style = TuripTypography.titleLarge,
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
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "로딩")
@Composable
fun Home_Loading_Preview() {
    val uiState = HomeUiState.Idle
    TuripTheme {
        HomeScreenLayout(
            uiState = uiState.copy(isLoading = true, errorUiState = ErrorUiState.None),
            onRetryLoadContents = {},
            onSearchClick = {},
            onContentClick = {},
            onRegionClick = {},
            onDomesticClick = {},
        )
    }
}

@Preview(showBackground = true, name = "성공")
@Composable
fun Home_Success_Preview() {
    val uiState =
        HomeUiState(
            isLoading = false,
            regionCategories = emptyList(),
            isDomesticSelected = true,
            usersLikeContents = emptyList(),
            errorUiState = ErrorUiState.None,
        )
    TuripTheme {
        HomeScreenLayout(
            uiState = uiState,
            onRetryLoadContents = {},
            onSearchClick = {},
            onContentClick = {},
            onRegionClick = {},
            onDomesticClick = {},
        )
    }
}

@Preview(showBackground = true, name = "서버 에러 발생")
@Composable
fun Home_Server_Error_Preview() {
    val uiState = HomeUiState.Idle
    TuripTheme {
        HomeScreenLayout(
            uiState = uiState.copy(isLoading = false, errorUiState = ErrorUiState.Server),
            onRetryLoadContents = {},
            onSearchClick = {},
            onContentClick = {},
            onRegionClick = {},
            onDomesticClick = {},
        )
    }
}

@Preview(showBackground = true, name = "네트워크 에러 발생")
@Composable
fun Home_Network_Error_Preview() {
    val uiState = HomeUiState.Idle
    TuripTheme {
        HomeScreenLayout(
            uiState = uiState.copy(isLoading = false, errorUiState = ErrorUiState.Network),
            onRetryLoadContents = {},
            onSearchClick = {},
            onContentClick = {},
            onRegionClick = {},
            onDomesticClick = {},
        )
    }
}
