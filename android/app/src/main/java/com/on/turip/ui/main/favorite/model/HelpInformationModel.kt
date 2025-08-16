package com.on.turip.ui.main.favorite.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HelpInformationModel(
    @DrawableRes val iconResource: Int,
    @StringRes val title: Int,
    val onClick: () -> Unit,
)
