package com.on.turip.feature.randomtravel.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_count
import com.on.turip.core.designsystem.generated.resources.random_travel_related_spot_more_description
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.randomtravel.impl.model.RandomTravelRelatedSpotModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * 브리핑의 연관 관광지 카드.
 *
 * 프로토타입(`turip-slot-flow.html`)의 행사 카드(`.ev`) 배치를 그대로 따른다.
 * 왼쪽 고정폭 라벨 · 세로 구분선 · 본문(대표 장소 + 나머지) · 오른쪽 개수 칩.
 */
@Composable
internal fun RandomTravelRelatedSpotItem(
    relatedSpot: RandomTravelRelatedSpotModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemShape = TuripTheme.shape.chip

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = TuripTheme.colors.container, shape = itemShape)
                .clip(itemShape)
                .clickable(onClick = onClick)
                .padding(TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = relatedSpot.category,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(CATEGORY_LABEL_WIDTH),
        )

        Box(
            modifier =
                Modifier
                    .padding(horizontal = TuripTheme.spacing.medium)
                    .width(DIVIDER_WIDTH)
                    .height(DIVIDER_HEIGHT)
                    .background(TuripTheme.colors.border),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = relatedSpot.headlineSpot,
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (relatedSpot.hasRemainSpots) {
                Text(
                    text = relatedSpot.remainSpots,
                    style = TuripTheme.typography.info1,
                    color = TuripTheme.colors.gray03,
                    maxLines = REMAIN_SPOTS_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
                )
            }
        }

        Surface(
            color = TuripTheme.colors.chipBackground,
            shape = itemShape,
            modifier = Modifier.padding(start = TuripTheme.spacing.small),
        ) {
            Text(
                text =
                    stringResource(Res.string.random_travel_related_spot_count)
                        .formatResource(relatedSpot.spots.size),
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray05,
                maxLines = 1,
                modifier =
                    Modifier.padding(
                        horizontal = TuripTheme.spacing.small,
                        vertical = TuripTheme.spacing.extraSmall,
                    ),
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(Res.string.random_travel_related_spot_more_description),
            tint = TuripTheme.colors.gray02,
            modifier = Modifier.size(CHEVRON_SIZE),
        )
    }
}

private val CATEGORY_LABEL_WIDTH = 46.dp
private val DIVIDER_WIDTH = 1.dp
private val DIVIDER_HEIGHT = 32.dp
private val CHEVRON_SIZE = 20.dp
private const val REMAIN_SPOTS_MAX_LINES: Int = 2

@Preview(showBackground = true, name = "연관 관광지 카드")
@Composable
private fun RandomTravelRelatedSpotItemPreview() {
    TuripTheme {
        RandomTravelRelatedSpotItem(
            relatedSpot =
                RandomTravelRelatedSpotModel(
                    category = "관광지",
                    spots =
                        persistentListOf(
                            "종묘",
                            "북촌한옥마을",
                            "광장시장",
                            "남산케이블카",
                            "국립중앙박물관",
                            "청계천",
                        ),
                ),
            onClick = {},
        )
    }
}
