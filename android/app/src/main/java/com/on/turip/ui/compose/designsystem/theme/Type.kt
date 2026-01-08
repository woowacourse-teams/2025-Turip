package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.on.turip.R

private val Pretendard =
    FontFamily(
        Font(R.font.pretendard_bold, FontWeight.Bold),
        Font(R.font.pretendard_semibold, FontWeight.SemiBold),
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_light, FontWeight.Light),
    )

private val PretendardStyle =
    TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
    )

@Immutable
data class TuripTypography(
    val display: TextStyle,
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val info1: TextStyle,
    val info2: TextStyle,
)

internal val Typography =
    TuripTypography(
        display =
            PretendardStyle.copy(
                fontSize = 26.sp,
                lineHeight = 33.8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.58).sp,
            ),
        title1 =
            PretendardStyle.copy(
                fontSize = 18.sp,
                lineHeight = 25.2.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.75).sp,
            ),
        title2 =
            PretendardStyle.copy(
                fontSize = 16.sp,
                lineHeight = 22.4.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.58).sp,
            ),
        title3 =
            PretendardStyle.copy(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.58).sp,
            ),
        body1 =
            PretendardStyle.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-0.58).sp,
            ),
        body2 =
            PretendardStyle.copy(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.75).sp,
            ),
        info1 =
            PretendardStyle.copy(
                fontSize = 12.sp,
                lineHeight = 16.8.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.58).sp,
            ),
        info2 =
            PretendardStyle.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.58).sp,
            ),
    )

val LocalTypography =
    staticCompositionLocalOf {
        TuripTypography(
            display = PretendardStyle,
            title1 = PretendardStyle,
            title2 = PretendardStyle,
            title3 = PretendardStyle,
            body1 = PretendardStyle,
            body2 = PretendardStyle,
            info1 = PretendardStyle,
            info2 = PretendardStyle,
        )
    }
