package com.on.turip.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.on.turip.R

private val PretendardBold =
    FontFamily(
        Font(R.font.pretendard_bold, FontWeight.Bold),
    )

private val PretendardLight =
    FontFamily(
        Font(R.font.pretendard_light, FontWeight.Light),
    )

private val PretendardRegular =
    FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
    )

val TuripTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 26.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            ),
        titleLarge =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 18.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold,
            ),
        titleMedium =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
            ),
        titleSmall =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = PretendardLight,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Light,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = PretendardLight,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Light,
            ),
        labelLarge =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
        labelMedium =
            TextStyle(
                fontFamily = PretendardRegular,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
        labelSmall =
            TextStyle(
                fontFamily = PretendardRegular,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
    )
