package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val LightColorScheme =
    lightColorScheme(
        primary = LightColors.primary,
        onPrimary = LightColors.white,
        primaryContainer = TuripColor.LightBlue,
        onPrimaryContainer = LightColors.black,
        inversePrimary = TuripColor.LightBlue,
        secondary = TuripColor.LightBlue,
        onSecondary = LightColors.white,
        background = LightColors.background,
        onBackground = LightColors.black,
        surface = LightColors.white,
        onSurface = LightColors.black,
        surfaceVariant = LightColors.container,
        onSurfaceVariant = LightColors.gray03,
        inverseSurface = LightColors.gray04,
        inverseOnSurface = LightColors.white,
        error = LightColors.error,
        onError = LightColors.white,
        errorContainer = LightColors.errorContainer,
        onErrorContainer = LightColors.error,
        outline = LightColors.border,
        scrim = LightColors.scrim,
        surfaceContainerLowest = LightColors.background,
        surfaceContainer = LightColors.container,
        surfaceContainerHighest = LightColors.white,
    )

@Composable
fun TuripTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalColors provides LightColors,
        LocalTypography provides Typography,
        LocalShape provides Shape,
        LocalSpacing provides Spacing,
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            content = content,
        )
    }
}

object TuripTheme {
    val typography: TuripTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val colors: TuripColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val shape: TuripShape
        @Composable
        @ReadOnlyComposable
        get() = LocalShape.current

    val spacing: TuripSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}
