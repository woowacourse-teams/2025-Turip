package com.on.turip.feature.randomtravel.impl.relatedspot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.component.TuripLoadingIndicator
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_detail_empty
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_detail_title
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_detail_total
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.util.formatResource
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RelatedSpotDetailScreen(
    regionCategoryName: String,
    spotCategory: String,
    onBackClick: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RelatedSpotDetailViewModel = koinViewModel(),
) {
    val uiState: RelatedSpotDetailState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(regionCategoryName, spotCategory) {
        viewModel.initRelatedSpot(
            regionCategoryName = regionCategoryName,
            spotCategory = spotCategory,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: RelatedSpotDetailEffect ->
            when (effect) {
                RelatedSpotDetailEffect.NavigateToLogin -> onNavigateToLoginScreen()
            }
        }
    }

    RelatedSpotDetailContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun RelatedSpotDetailContent(
    uiState: RelatedSpotDetailState,
    onIntent: (RelatedSpotDetailIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
    ) {
        TuripAppBar(
            start = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(APP_BAR_ICON_SIZE),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.all_back_description),
                        tint = TuripTheme.colors.black,
                    )
                }
            },
            center = {
                Text(
                    text =
                        stringResource(Res.string.random_travel_related_spot_detail_title)
                            .formatResource(uiState.regionCategoryName, uiState.spotCategory),
                    style = TuripTheme.typography.title1,
                    color = TuripTheme.colors.black,
                )
            },
        )

        when {
            uiState.shouldShowErrorScreen -> {
                ErrorScreen(
                    errorUiState = uiState.errorUiState,
                    onRetryClick = { onIntent(RelatedSpotDetailIntent.Retry) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    TuripLoadingIndicator()
                }
            }

            uiState.shouldShowEmpty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.random_travel_related_spot_detail_empty),
                        style = TuripTheme.typography.body1,
                        color = TuripTheme.colors.gray03,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                RelatedSpotList(
                    spots = uiState.spots,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun RelatedSpotList(
    spots: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding =
            PaddingValues(
                horizontal = TuripTheme.spacing.extraLarge,
                vertical = TuripTheme.spacing.large,
            ),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
    ) {
        item(key = TOTAL_COUNT_ITEM_KEY) {
            Surface(
                color = TuripTheme.colors.chipBackground,
                shape = TuripTheme.shape.chip,
                modifier = Modifier.padding(bottom = TuripTheme.spacing.small),
            ) {
                Text(
                    text =
                        stringResource(Res.string.random_travel_related_spot_detail_total)
                            .formatResource(spots.size),
                    style = TuripTheme.typography.info1,
                    color = TuripTheme.colors.gray05,
                    modifier =
                        Modifier.padding(
                            horizontal = TuripTheme.spacing.medium,
                            vertical = TuripTheme.spacing.extraSmall,
                        ),
                )
            }
        }

        itemsIndexed(spots) { index, spot ->
            RelatedSpotRow(order = index + 1, spot = spot)
        }
    }
}

/**
 * 프로토타입 코스 목록(`.stop`)의 번호 원형 배지를 그대로 쓴다.
 */
@Composable
private fun RelatedSpotRow(
    order: Int,
    spot: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = TuripTheme.colors.container,
                    shape = TuripTheme.shape.chip,
                ).padding(TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(ORDER_BADGE_SIZE)
                    .background(color = TuripTheme.colors.primary, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = order.toString(),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.white,
            )
        }

        Text(
            text = spot,
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.black,
            modifier =
                Modifier
                    .padding(start = TuripTheme.spacing.medium)
                    .weight(1f),
        )
    }
}

private val APP_BAR_ICON_SIZE = 36.dp
private val ORDER_BADGE_SIZE = 24.dp
private const val TOTAL_COUNT_ITEM_KEY: String = "total_count"

@Preview(showBackground = true, name = "연관 관광지 전체 목록")
@Composable
private fun RelatedSpotDetailPreview() {
    TuripTheme {
        RelatedSpotDetailContent(
            uiState =
                RelatedSpotDetailState(
                    regionCategoryName = "서울",
                    spotCategory = "관광지",
                    spots =
                        persistentListOf(
                            "종묘",
                            "북촌한옥마을",
                            "광장시장",
                            "남산케이블카",
                            "국립중앙박물관",
                            "청계천",
                            "서대문형무소역사관",
                        ),
                    isLoading = false,
                    isFetched = true,
                ),
            onIntent = {},
            onBackClick = {},
        )
    }
}
