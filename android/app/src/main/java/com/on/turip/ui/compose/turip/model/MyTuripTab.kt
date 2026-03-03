package com.on.turip.ui.compose.turip.model

import androidx.annotation.StringRes
import com.on.turip.R

enum class MyTuripTab(
    @field:StringRes val tabRes: Int,
) {
    ALL(R.string.turip_tab_all),
    SOLO(R.string.turip_tab_solo_turip),
    TOGETHER(R.string.turip_tab_together_turip),
}
