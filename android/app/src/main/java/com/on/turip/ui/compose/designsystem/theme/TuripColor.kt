package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 *  변수명 뒤에 숫자 2개인 경우 (높아질수록 짙은 색 / 단, 00인 경우 변수 색상과 완전 동일 색상)
 *
 *  변수명 뒤에 숫자 4개인 경우 (앞 2숫자 색상, 뒤 2숫자 투명도)
 */
internal object TuripColor {
    @Stable
    val White = Color(0xFFFFFFFF)

    @Stable
    val Black = Color(0xFF151515)

    @Stable
    val Black0050 = Color(0x80000000)

    @Stable
    val Black06 = Color(0x0F000000)

    @Stable
    val Red = Color(0xFFFF7474)

    @Stable
    val Blue = Color(0xFF5AC3D5)

    @Stable
    val LightBlue = Color(0x335AC3D5)

    @Stable
    val Gray05 = Color(0xFF1E1E1E)

    @Stable
    val Gray04 = Color(0xFF2B2B2B)

    @Stable
    val Gray03 = Color(0xFF5B5B5B)

    @Stable
    val Gray02 = Color(0xFFC1C1C1)

    @Stable
    val Gray01 = Color(0xFFF0F0EE)

    @Stable
    val LightGray = Color(0xFFF9F9F9)

    @Stable
    val LightGray01 = Color(0x80F2F2F2)

    @Stable
    val LightBeige = Color(0xFFFFF4B2)
}

@Immutable
data class TuripColors(
    val primary: Color,
    val primarySub: Color,
    val background: Color,
    val error: Color,
    val errorContainer: Color,
    val chipBackground: Color,
    val scrim: Color,
    val container: Color,
    val border: Color,
    val white: Color,
    val black: Color,
    val gray05: Color,
    val gray04: Color,
    val gray03: Color,
    val gray02: Color,
    val gray01: Color,
)

internal val LightColors =
    TuripColors(
        primary = TuripColor.Blue,
        primarySub = TuripColor.LightBlue,
        background = TuripColor.White,
        error = TuripColor.Red,
        errorContainer = TuripColor.Red.copy(alpha = 0.12f),
        chipBackground = TuripColor.LightBeige,
        scrim = TuripColor.Black0050,
        container = TuripColor.LightGray,
        border = TuripColor.Gray01,
        white = TuripColor.White,
        black = TuripColor.Black,
        gray05 = TuripColor.Gray05,
        gray04 = TuripColor.Gray04,
        gray03 = TuripColor.Gray03,
        gray02 = TuripColor.Gray02,
        gray01 = TuripColor.Gray01,
    )

val LocalColors =
    staticCompositionLocalOf {
        TuripColors(
            primary = TuripColor.Black,
            primarySub = TuripColor.Black,
            background = TuripColor.Black,
            error = TuripColor.Black,
            errorContainer = TuripColor.Black,
            chipBackground = TuripColor.Black,
            scrim = TuripColor.Black,
            container = TuripColor.Black,
            border = TuripColor.Black,
            white = TuripColor.Black,
            black = TuripColor.Black,
            gray05 = TuripColor.Black,
            gray04 = TuripColor.Black,
            gray03 = TuripColor.Black,
            gray02 = TuripColor.Black,
            gray01 = TuripColor.Black,
        )
    }

@Preview(name = "Turip Colors", heightDp = 1000)
@Composable
private fun ColorPalettePreview() {
    TuripTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ColorPaletteContent()
        }
    }
}

@Composable
private fun ColorPaletteContent() {
    val colors = LocalColors.current

    Column(
        modifier =
            Modifier
                .padding(TuripTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
    ) {
        Text(
            text = "Turip Color Palette",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = TuripTheme.spacing.small),
        )

        ColorGroup(title = "Primary") {
            ColorItem("primary", colors.primary)
            ColorItem("primarySub", colors.primarySub)
        }

        ColorGroup(title = "Background / Container") {
            ColorItem("background", colors.background)
            ColorItem("container", colors.container)
            ColorItem("chipBackground", colors.chipBackground)
            ColorItem("border", colors.border)
            ColorItem("scrim", colors.scrim)
        }

        ColorGroup(title = "Error") {
            ColorItem("error", colors.error)
            ColorItem("errorContainer", colors.errorContainer)
        }

        ColorGroup(title = "Base Colors") {
            ColorItem("white", colors.white)
            ColorItem("black", colors.black)
        }

        ColorGroup(title = "Gray Scale") {
            ColorItem("gray05", colors.gray05)
            ColorItem("gray04", colors.gray04)
            ColorItem("gray03", colors.gray03)
            ColorItem("gray02", colors.gray02)
            ColorItem("gray01", colors.gray01)
        }
    }
}

@Composable
private fun ColorGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = TuripTheme.spacing.extraSmall),
        )
        content()
    }
}

@Composable
private fun ColorItem(
    name: String,
    color: Color,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .weight(0.3f)
                    .height(40.dp)
                    .background(color),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
                Modifier
                    .weight(0.7f)
                    .padding(horizontal = 12.dp),
        )
    }
}
