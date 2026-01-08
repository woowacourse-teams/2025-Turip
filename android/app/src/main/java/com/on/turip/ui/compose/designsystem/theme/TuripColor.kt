package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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
    val LightBeige = Color(0xFFFFF4B2)
}

@Immutable
data class TuripColors(
    val primary: Color,
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
