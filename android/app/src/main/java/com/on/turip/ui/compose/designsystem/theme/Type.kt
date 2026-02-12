package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Preview(name = "Typography", heightDp = 550)
@Composable
private fun TypographyDarkPreview() {
    TuripTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TypographyContent()
        }
    }
}

@Composable
private fun TypographyContent() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Typography Scale",
            style = TuripTheme.typography.display,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        TypographySection(title = "Title") {
            TypographyItem(
                label = "Title 1",
                style = TuripTheme.typography.title1,
            )
            TypographyItem(
                label = "Title 2",
                style = TuripTheme.typography.title2,
            )
            TypographyItem(
                label = "Title 3",
                style = TuripTheme.typography.title3,
            )
        }

        TypographySection(title = "Body") {
            TypographyItem(
                label = "Body 1",
                style = TuripTheme.typography.body1,
                sampleText = "Body 1",
            )
            TypographyItem(
                label = "Body 2",
                style = TuripTheme.typography.body2,
                sampleText = "Body 2",
            )
        }

        TypographySection(title = "Info") {
            TypographyItem(
                label = "Info 1",
                style = TuripTheme.typography.info1,
                sampleText = "Info 1",
            )
            TypographyItem(
                label = "Info 2",
                style = TuripTheme.typography.info2,
                sampleText = "Info 2",
            )
        }
    }
}

@Composable
private fun TypographySection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        content()
    }
}

@Composable
private fun TypographyItem(
    label: String,
    style: TextStyle,
    sampleText: String = label,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = sampleText,
            style = style,
        )
    }
}
