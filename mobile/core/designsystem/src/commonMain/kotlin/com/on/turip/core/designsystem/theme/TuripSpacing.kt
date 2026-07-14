package com.on.turip.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
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
 * @param huge 28dp
 * @param extraHuge 32dp
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
    val extraHuge: Dp,
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
        huge = 28.dp,
        extraHuge = 32.dp,
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
            extraHuge = 0.dp,
        )
    }
