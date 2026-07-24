package com.on.turip.feature.turipdetail.impl.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

enum class ProfileRowStyle {
    SMALL,
    MEDIUM,
    LARGE,
    ;

    companion object {
        @Composable
        fun ProfileRowStyle.avatarSize(): Dp =
            when (this) {
                SMALL -> 28.dp
                MEDIUM -> 36.dp
                LARGE -> 48.dp
            }

        @Composable
        fun ProfileRowStyle.textStyle() =
            when (this) {
                SMALL -> TuripTheme.typography.title3
                MEDIUM -> TuripTheme.typography.title2
                LARGE -> TuripTheme.typography.title1
            }
    }
}
