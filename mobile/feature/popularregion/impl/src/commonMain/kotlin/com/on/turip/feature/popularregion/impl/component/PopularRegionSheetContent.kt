package com.on.turip.feature.popularregion.impl.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_location
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_base_month_label
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_contents_empty
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_contents_error
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_contents_retry
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_contents_title
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_contents_unsupported
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_popular_badge
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_related_contents
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_tag_content_count
import com.on.turip.core.designsystem.generated.resources.popular_region_sheet_tag_hot
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.popularregion.impl.map.GeoPoint
import com.on.turip.feature.popularregion.impl.model.PopularRegionModel
import com.on.turip.feature.popularregion.impl.model.RegionContentModel
import com.on.turip.feature.popularregion.impl.model.RegionContentsUiState
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 지도에서 지역을 눌렀을 때 올라오는 시트의 본문.
 *
 * 시트를 열고 닫는 책임은 화면이 갖는다. 여기서는 `선택된 지역이 있다`는 전제로 내용만 그린다.
 *
 * 방문자 수는 시도 전체에 있지만 영상은 튜립이 다루는 지역에만 있어, 아래 두 영역의 데이터 출처가 다르다.
 */
@Composable
internal fun PopularRegionSheetContent(
    region: PopularRegionModel,
    isTopRegion: Boolean,
    baseMonthText: String,
    contentsUiState: RegionContentsUiState,
    onRelatedContentsClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onRetryContentsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = TuripTheme.spacing.extraLarge),
    ) {
        RegionTitleRow(
            regionName = region.name,
            isTopRegion = isTopRegion,
            modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
        )

        RegionStatCard(
            region = region,
            baseMonthText = baseMonthText,
            onRelatedContentsClick = onRelatedContentsClick,
            modifier =
                Modifier
                    .padding(
                        start = TuripTheme.spacing.extraLarge,
                        end = TuripTheme.spacing.extraLarge,
                        top = TuripTheme.spacing.large,
                    ),
        )

        RegionTagRow(
            contentsUiState = contentsUiState,
            modifier =
                Modifier
                    .padding(
                        start = TuripTheme.spacing.extraLarge,
                        end = TuripTheme.spacing.extraLarge,
                        top = TuripTheme.spacing.large,
                    ),
        )

        Text(
            text =
                stringResource(Res.string.popular_region_sheet_contents_title)
                    .formatResource(region.name),
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.black,
            modifier =
                Modifier.padding(
                    start = TuripTheme.spacing.extraLarge,
                    end = TuripTheme.spacing.extraLarge,
                    top = TuripTheme.spacing.extraExtraLarge,
                ),
        )

        // 로딩 자리표시자와 실제 카드의 높이가 달라, 값이 도착할 때 시트가 덜컥 늘어난다.
        // 높이 변화를 애니메이션으로 이어 준다.
        RegionContentsSection(
            contentsUiState = contentsUiState,
            onContentClick = onContentClick,
            onRetryClick = onRetryContentsClick,
            modifier = Modifier.animateContentSize(),
        )
    }
}

@Composable
private fun RegionContentsSection(
    contentsUiState: RegionContentsUiState,
    onContentClick: (contentId: Long) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (contentsUiState) {
            RegionContentsUiState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(CONTENTS_PLACEHOLDER_HEIGHT),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = TuripTheme.colors.primary,
                        modifier = Modifier.size(PROGRESS_SIZE),
                    )
                }
            }

            RegionContentsUiState.Unsupported -> {
                ContentsMessage(text = stringResource(Res.string.popular_region_sheet_contents_unsupported))
            }

            RegionContentsUiState.Error -> {
                Column(modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge)) {
                    Text(
                        text = stringResource(Res.string.popular_region_sheet_contents_error),
                        style = TuripTheme.typography.body2,
                        color = TuripTheme.colors.gray03,
                        modifier = Modifier.padding(vertical = TuripTheme.spacing.large),
                    )
                    Text(
                        text = stringResource(Res.string.popular_region_sheet_contents_retry),
                        style = TuripTheme.typography.title3,
                        color = TuripTheme.colors.primary,
                        modifier =
                            Modifier
                                .clip(TuripTheme.shape.wideButton)
                                .clickable(onClick = onRetryClick)
                                .padding(
                                    horizontal = TuripTheme.spacing.medium,
                                    vertical = TuripTheme.spacing.small,
                                ),
                    )
                }
            }

            is RegionContentsUiState.Success -> {
                if (contentsUiState.contents.isEmpty()) {
                    ContentsMessage(text = stringResource(Res.string.popular_region_sheet_contents_empty))
                    return@Column
                }

                LazyRow(
                    modifier = Modifier.padding(top = TuripTheme.spacing.medium),
                    contentPadding = PaddingValues(horizontal = TuripTheme.spacing.extraLarge),
                    horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                ) {
                    items(
                        items = contentsUiState.contents,
                        key = { it.contentId },
                    ) { content ->
                        RegionContentCard(
                            content = content,
                            onClick = { onContentClick(content.contentId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentsMessage(text: String) {
    Text(
        text = text,
        style = TuripTheme.typography.body2,
        color = TuripTheme.colors.gray03,
        modifier =
            Modifier.padding(
                horizontal = TuripTheme.spacing.extraLarge,
                vertical = TuripTheme.spacing.large,
            ),
    )
}

@Composable
private fun RegionTitleRow(
    regionName: String,
    isTopRegion: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_location),
            contentDescription = null,
            tint = TuripTheme.colors.black,
            modifier = Modifier.size(TITLE_ICON_SIZE),
        )
        Text(
            text = regionName,
            style = TuripTheme.typography.display,
            color = TuripTheme.colors.black,
            modifier = Modifier.padding(start = TuripTheme.spacing.small),
        )
        if (isTopRegion) {
            TagChip(
                text = stringResource(Res.string.popular_region_sheet_popular_badge),
                background = TuripTheme.colors.chipBackground,
                contentColor = TuripTheme.colors.gray05,
                modifier = Modifier.padding(start = TuripTheme.spacing.small),
            )
        }
    }
}

/**
 * 방문자 수는 기준월이 없으면 무엇과 견줄 값인지 알 수 없으므로 라벨에 기준월을 함께 적는다.
 *
 * `연관 콘텐츠 보기`는 콘텐츠를 가진 지역에만 둔다. 갈 곳이 없는 버튼을 눌리게 두지 않는다.
 */
@Composable
private fun RegionStatCard(
    region: PopularRegionModel,
    baseMonthText: String,
    onRelatedContentsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = TuripTheme.colors.container,
                    shape = TuripTheme.shape.largeContainer,
                ).padding(TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatColumn(
            label =
                stringResource(Res.string.popular_region_sheet_base_month_label)
                    .formatResource(baseMonthText),
            value = region.visitorCountText,
            valueColor = TuripTheme.colors.black,
            modifier = Modifier.weight(1f),
        )
        if (region.hasRegionCategory) {
            RelatedContentsButton(
                onClick = onRelatedContentsClick,
                modifier = Modifier.padding(start = TuripTheme.spacing.medium),
            )
        }
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray03,
        )
        Text(
            text = value,
            style = TuripTheme.typography.title1,
            color = valueColor,
            modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
        )
    }
}

@Composable
private fun RelatedContentsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(
                    color = TuripTheme.colors.primary,
                    shape = TuripTheme.shape.wideButton,
                ).clip(TuripTheme.shape.wideButton)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = TuripTheme.spacing.large,
                    vertical = TuripTheme.spacing.medium,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.popular_region_sheet_related_contents),
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.white,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TuripTheme.colors.white,
            modifier = Modifier.size(ARROW_SIZE),
        )
    }
}

/** 콘텐츠 개수는 실제로 받아온 뒤에만 말할 수 있으므로 성공 상태에서만 칩을 세운다. */
@Composable
private fun RegionTagRow(
    contentsUiState: RegionContentsUiState,
    modifier: Modifier = Modifier,
) {
    val contents: List<RegionContentModel> =
        (contentsUiState as? RegionContentsUiState.Success)?.contents ?: return

    if (contents.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (contents.size >= HOT_PLACE_CONTENT_COUNT) {
            TagChip(
                text = stringResource(Res.string.popular_region_sheet_tag_hot),
                background = TuripTheme.colors.chipBackground,
                contentColor = TuripTheme.colors.gray05,
            )
        }
        TagChip(
            text =
                stringResource(Res.string.popular_region_sheet_tag_content_count)
                    .formatResource(contents.size),
            background = TuripTheme.colors.border,
            contentColor = TuripTheme.colors.gray04,
        )
    }
}

@Composable
private fun TagChip(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = TuripTheme.typography.info1,
        color = contentColor,
        modifier =
            modifier
                .background(color = background, shape = TuripTheme.shape.wideButton)
                .padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.extraSmall,
                ),
    )
}

private const val HOT_PLACE_CONTENT_COUNT: Int = 3

private val TITLE_ICON_SIZE = 24.dp
private val ARROW_SIZE = 18.dp
private val PROGRESS_SIZE = 28.dp
private val CONTENTS_PLACEHOLDER_HEIGHT = 120.dp

@Preview(showBackground = true)
@Composable
private fun PopularRegionSheetContentPreview() {
    TuripTheme {
        PopularRegionSheetContent(
            region =
                PopularRegionModel(
                    code = "51",
                    name = "강원",
                    location = GeoPoint(37.8228, 128.1555),
                    visitorCount = 7_600_000L,
                    regionCategoryNames = persistentListOf("강릉", "속초"),
                ),
            isTopRegion = true,
            baseMonthText = "2025년 6월",
            contentsUiState =
                RegionContentsUiState.Success(
                    persistentListOf(
                        RegionContentModel(
                            contentId = 1L,
                            title = "강릉 바다 따라 힐링 여행 브이로그",
                            thumbnailUrl = "",
                            creatorName = "바다좋아 튜립팀",
                            uploadedDate = "2026-05-12",
                            nights = 2,
                            days = 3,
                            placeCount = 15,
                            isBookmarked = false,
                        ),
                        RegionContentModel(
                            contentId = 2L,
                            title = "속초 감성 카페 투어 브이로그 ☕",
                            thumbnailUrl = "",
                            creatorName = "감성여행러",
                            uploadedDate = "2026-04-28",
                            nights = 1,
                            days = 2,
                            placeCount = 11,
                            isBookmarked = true,
                        ),
                    ),
                ),
            onRelatedContentsClick = {},
            onContentClick = {},
            onRetryContentsClick = {},
            modifier = Modifier.padding(vertical = TuripTheme.spacing.large),
        )
    }
}
