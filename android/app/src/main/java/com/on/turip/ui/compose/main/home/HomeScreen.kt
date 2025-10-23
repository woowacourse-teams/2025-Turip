package com.on.turip.ui.compose.main.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.on.turip.R
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.compose.common.component.ErrorHandlingContainer
import com.on.turip.ui.compose.common.component.SearchTextField
import com.on.turip.ui.compose.main.home.component.HomeTopAppBar
import com.on.turip.ui.compose.main.home.component.RegionList
import com.on.turip.ui.compose.main.home.component.RegionTypeButtons
import com.on.turip.ui.compose.main.home.component.UsersLikeList
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.compose.theme.TuripTypography
import com.on.turip.ui.main.home.HomeViewModel
import com.on.turip.ui.main.home.model.UsersLikeContentModel

@Composable
fun HomeScreen(
    onSearchClick: (String) -> Unit,
    onRegionClick: (String) -> Unit,
    onContentClick: (UsersLikeContentModel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    var keyword: String by rememberSaveable { mutableStateOf("") }

    val regions: List<RegionCategory> by viewModel.regionCategories.observeAsState(emptyList())
    val isSelectedDomestic: Boolean by viewModel.isSelectedDomestic.observeAsState(true)
    val usersLikeContents: List<UsersLikeContentModel> by viewModel.usersLikeContents.observeAsState(
        emptyList(),
    )
    val networkError by viewModel.networkError.observeAsState(false)
    val serverError by viewModel.serverError.observeAsState(false)

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = { HomeTopAppBar() },
        modifier =
            Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
    ) { innerPadding ->
        ErrorHandlingContainer(
            networkError = networkError,
            serverError = serverError,
            onRetryClick = { viewModel.reload() },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = 20.dp,
                            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                        )
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
                    usersLikeContents = usersLikeContents,
                    onContentClick = onContentClick,
                )

                RegionTypeButtons(
                    onDomesticClick = { isSelectDomestic: Boolean ->
                        viewModel.updateDomesticSelected(isSelectDomestic)
                    },
                    isSelectedDomestic = isSelectedDomestic,
                )

                RegionList(
                    regions = regions,
                    onRegionClick = onRegionClick,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TuripTheme {
        HomeScreen({}, {}, {})
    }
}
