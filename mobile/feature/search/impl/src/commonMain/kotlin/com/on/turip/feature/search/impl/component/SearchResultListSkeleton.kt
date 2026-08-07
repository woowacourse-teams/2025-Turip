package com.on.turip.feature.search.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.component.rememberSkeletonAlpha
import com.on.turip.core.designsystem.theme.TuripTheme

private const val SEARCH_RESULT_SKELETON_ITEM_COUNT = 3

@Composable
fun SearchResultListSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberSkeletonAlpha(label = "searchResultListSkeleton")

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .graphicsLayer(alpha = alpha),
    ) {
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier =
                Modifier
                    .padding(
                        horizontal = TuripTheme.spacing.extraLarge,
                        vertical = TuripTheme.spacing.medium,
                    ).width(96.dp)
                    .height(16.dp),
        )

        repeat(SEARCH_RESULT_SKELETON_ITEM_COUNT) {
            SearchResultItemSkeleton(
                modifier = Modifier.padding(bottom = TuripTheme.spacing.large),
            )
        }
    }
}

@Composable
private fun SearchResultItemSkeleton(modifier: Modifier = Modifier) {
    val itemShape = TuripTheme.shape.wideButton

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.small,
                ).background(
                    color = TuripTheme.colors.cardBackground,
                    shape = itemShape,
                ).border(
                    width = 1.dp,
                    color = TuripTheme.colors.cardBorder,
                    shape = itemShape,
                ).padding(TuripTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            SkeletonBox(
                shape = CircleShape,
                modifier = Modifier.size(42.dp),
            )

            Column(
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.extraSmall)
                        .weight(1f),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
            ) {
                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.fillMaxWidth(0.7f).height(18.dp),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(140.dp).height(14.dp),
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .padding(top = TuripTheme.spacing.medium)
                    .fillMaxWidth()
                    .height(110.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.size(width = 202.dp, height = 110.dp),
            )

            Spacer(modifier = Modifier.width(TuripTheme.spacing.extraLarge))

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = TuripTheme.spacing.medium),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                SkeletonBox(
                    shape = TuripTheme.shape.wideButton,
                    modifier = Modifier.width(56.dp).height(22.dp),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(100.dp).height(14.dp),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(80.dp).height(14.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchResultListSkeletonPreview() {
    TuripTheme {
        SearchResultListSkeleton()
    }
}
