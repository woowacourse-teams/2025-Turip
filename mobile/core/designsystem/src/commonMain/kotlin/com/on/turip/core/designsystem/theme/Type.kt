package com.on.turip.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.pretendard_bold
import com.on.turip.core.designsystem.generated.resources.pretendard_light
import com.on.turip.core.designsystem.generated.resources.pretendard_regular
import com.on.turip.core.designsystem.generated.resources.pretendard_semibold
import org.jetbrains.compose.resources.Font

@Composable
internal fun rememberTuripTypography(): TuripTypography {
    val pretendard =
        FontFamily(
            Font(Res.font.pretendard_bold, FontWeight.Bold),
            Font(Res.font.pretendard_semibold, FontWeight.SemiBold),
            Font(Res.font.pretendard_regular, FontWeight.Normal),
            Font(Res.font.pretendard_light, FontWeight.Light),
        )
    val base = TextStyle(fontFamily = pretendard, fontWeight = FontWeight.Normal)
    return TuripTypography(
        display =
            base.copy(
                fontSize = 26.sp,
                lineHeight = 33.8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.58).sp,
            ),
        title1 =
            base.copy(
                fontSize = 18.sp,
                lineHeight = 25.2.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.75).sp,
            ),
        title2 =
            base.copy(
                fontSize = 16.sp,
                lineHeight = 22.4.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.58).sp,
            ),
        title3 =
            base.copy(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.58).sp,
            ),
        body1 =
            base.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-0.58).sp,
            ),
        body2 =
            base.copy(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.75).sp,
            ),
        info1 =
            base.copy(
                fontSize = 12.sp,
                lineHeight = 16.8.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.58).sp,
            ),
        info2 =
            base.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.58).sp,
            ),
    )
}

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

val LocalTypography =
    staticCompositionLocalOf {
        TuripTypography(
            display = TextStyle(),
            title1 = TextStyle(),
            title2 = TextStyle(),
            title3 = TextStyle(),
            body1 = TextStyle(),
            body2 = TextStyle(),
            info1 = TextStyle(),
            info2 = TextStyle(),
        )
    }
