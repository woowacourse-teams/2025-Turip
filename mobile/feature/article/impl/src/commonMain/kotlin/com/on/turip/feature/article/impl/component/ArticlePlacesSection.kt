package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.article_detail_places_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.article.impl.model.ArticlePlaceUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArticlePlacesSection(
    places: ImmutableList<ArticlePlaceUiModel>,
    onPlaceMapClick: (place: ArticlePlaceUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (places.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.article_detail_places_title),
            color = TuripTheme.colors.gray04,
            style = TuripTheme.typography.title1,
            modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        Column(verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium)) {
            places.forEach { place: ArticlePlaceUiModel ->
                ArticlePlaceCard(
                    place = place,
                    onMapClick = { onPlaceMapClick(place) },
                    modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticlePlacesSectionPreview() {
    TuripTheme {
        ArticlePlacesSection(
            places =
                persistentListOf(
                    ArticlePlaceUiModel(1L, "속초해수욕장", "강원 속초시 조양동", "해수욕장", ""),
                    ArticlePlaceUiModel(2L, "영금정", "강원 속초시 영금정로 43", "관광지", ""),
                ),
            onPlaceMapClick = {},
        )
    }
}
