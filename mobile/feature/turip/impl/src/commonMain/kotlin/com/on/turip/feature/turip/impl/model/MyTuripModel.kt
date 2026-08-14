package com.on.turip.feature.turip.impl.model

import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_individual_folder
import com.on.turip.core.designsystem.generated.resources.ic_together_folder
import com.on.turip.core.model.turip.TuripType
import org.jetbrains.compose.resources.DrawableResource

data class MyTuripModel(
    val id: Long,
    val name: String,
    val type: TuripType,
    val isDefault: Boolean,
    val memberCount: Int = 0,
    val placeCount: Int = 0,
) {
    val image: DrawableResource
        get() =
            when (type) {
                TuripType.SOLO -> Res.drawable.ic_individual_folder
                TuripType.TOGETHER -> Res.drawable.ic_together_folder
            }

    companion object {
        val Idle: MyTuripModel =
            MyTuripModel(id = -1L, name = "", type = TuripType.SOLO, isDefault = false)
    }
}
