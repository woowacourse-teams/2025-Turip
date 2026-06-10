package com.on.turip.feature.turip.impl.model

import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.turip_tab_all
import com.on.turip.core.designsystem.generated.resources.turip_tab_solo_turip
import com.on.turip.core.designsystem.generated.resources.turip_tab_together_turip
import org.jetbrains.compose.resources.StringResource

enum class MyTuripTab(
    val tabRes: StringResource,
) {
    ALL(Res.string.turip_tab_all),
    SOLO(Res.string.turip_tab_solo_turip),
    TOGETHER(Res.string.turip_tab_together_turip),
}
