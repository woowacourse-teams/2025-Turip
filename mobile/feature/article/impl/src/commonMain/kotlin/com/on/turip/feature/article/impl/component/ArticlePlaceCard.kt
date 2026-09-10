package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.article_detail_place_map_description
import com.on.turip.core.designsystem.generated.resources.ic_location
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.article.impl.model.ArticlePlaceUiModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val MAP_ICON_SIZE: Dp = 20.dp
private val MAP_BUTTON_SIZE: Dp = 36.dp

/**
 * 가로형 장소 카드. "이 아티클의 장소" 섹션에 세로로 나열된다.
 * 좌우 여백은 호출부에서 준다.
 */
@Composable
fun ArticlePlaceCard(
    place: ArticlePlaceUiModel,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clip(TuripTheme.shape.chip)
                .clickable { onMapClick() }
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = TuripTheme.shape.chip,
                ).padding(TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name,
                color = TuripTheme.colors.black,
                style = TuripTheme.typography.title3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = place.address,
                color = TuripTheme.colors.gray03,
                style = TuripTheme.typography.info1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (place.category.isNotBlank()) {
                Text(
                    text = place.category,
                    color = TuripTheme.colors.primary,
                    style = TuripTheme.typography.info1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Icon(
            painter = painterResource(Res.drawable.ic_location),
            contentDescription = stringResource(Res.string.article_detail_place_map_description),
            tint = TuripTheme.colors.primary,
            modifier = Modifier.size(MAP_ICON_SIZE),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticlePlaceCardPreview() {
    TuripTheme {
        ArticlePlaceCard(
            place =
                ArticlePlaceUiModel(
                    id = 1L,
                    name = "기막힌 닭",
                    address = "강원특별자치도 양양군 현남면 인구리 1-51",
                    category = "닭요리",
                    mapUrl = "",
                ),
            onMapClick = {},
        )
    }
}
