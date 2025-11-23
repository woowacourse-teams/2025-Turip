package com.on.turip.ui.compose.setting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SettingModel(
    @DrawableRes val iconResource: Int,
    @StringRes val titleResource: Int,
)
