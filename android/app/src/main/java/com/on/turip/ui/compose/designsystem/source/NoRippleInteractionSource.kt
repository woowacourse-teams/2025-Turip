package com.on.turip.ui.compose.designsystem.source

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 상호작용 이벤트를 보내지 않아 리플 효과를 비활성화하는 [androidx.compose.foundation.interaction.MutableInteractionSource]
 */
object NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}
