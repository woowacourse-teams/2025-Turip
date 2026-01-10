package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @param none 0dp
 * @param extraSmall 4dp
 * @param small 8dp
 * @param medium 12dp
 * @param large 16dp
 * @param extraLarge 20dp
 * @param extraExtraLarge 24dp
 * @param huge 32dp
 */

@Immutable
data class TuripSpacing(
    val none: Dp,
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
    val extraExtraLarge: Dp,
    val huge: Dp,
)

internal val Spacing =
    TuripSpacing(
        none = 0.dp,
        extraSmall = 4.dp,
        small = 8.dp,
        medium = 12.dp,
        large = 16.dp,
        extraLarge = 20.dp,
        extraExtraLarge = 24.dp,
        huge = 32.dp,
    )

internal val LocalSpacing =
    staticCompositionLocalOf {
        TuripSpacing(
            none = 0.dp,
            extraSmall = 0.dp,
            small = 0.dp,
            medium = 0.dp,
            large = 0.dp,
            extraLarge = 0.dp,
            extraExtraLarge = 0.dp,
            huge = 0.dp,
        )
    }

@Preview(name = "Spacing Tokens", heightDp = 800)
@Composable
private fun SpacingPreview() {
    TuripTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .padding(TuripTheme.spacing.large),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
            ) {
                Text(
                    text = "Spacing Tokens",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(TuripTheme.spacing.small),
                )

                SpacingItem("none", TuripTheme.spacing.none)
                SpacingItem("extraSmall", TuripTheme.spacing.extraSmall)
                SpacingItem("small", TuripTheme.spacing.small)
                SpacingItem("medium", TuripTheme.spacing.medium)
                SpacingItem("large", TuripTheme.spacing.large)
                SpacingItem("extraLarge", TuripTheme.spacing.extraLarge)
                SpacingItem("extraExtraLarge", TuripTheme.spacing.extraExtraLarge)
                SpacingItem("huge", TuripTheme.spacing.huge)
            }
        }
    }
}

@Composable
private fun SpacingItem(
    name: String,
    value: Dp,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
    ) {
        Text(
            text = "$name ($value)",
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(value)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.primary),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.outline),
            )
        }
    }
}
