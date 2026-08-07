package com.on.turip.feature.trip.impl.turipselection.util.reorderable

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch

@Stable
class ReorderableLazyColumnItemScope(
    private val reorderableLazyColumnState: ReorderableLazyColumnState,
    private val key: Long,
    private val itemPositionYProvider: () -> Float,
    private val enabled: Boolean = true,
) {
    fun Modifier.draggableAfterLongPress(
        interactionSource: MutableInteractionSource?,
        onDragStart: () -> Unit = {},
        onDragEnd: () -> Unit = {},
    ): Modifier =
        composed {
            var dragAreaOffsetY by remember { mutableFloatStateOf(0f) }
            var dragAreaHeight by remember { mutableIntStateOf(0) }

            val coroutineScope = rememberCoroutineScope()
            val haptic = LocalHapticFeedback.current

            onGloballyPositioned {
                dragAreaOffsetY = it.positionInRoot().y
                dragAreaHeight = it.size.height
            }.detectDragGesturesAfterLongPress(
                key = reorderableLazyColumnState,
                enabled = enabled && (reorderableLazyColumnState.isDragging(key).value || !reorderableLazyColumnState.isAnyItemDragging),
                interactionSource = interactionSource,
                onDragStart = {
                    coroutineScope.launch {
                        val dragAreaOffsetYToItem = dragAreaOffsetY - itemPositionYProvider()
                        val dragAreaCenterOffsetY = dragAreaOffsetYToItem + dragAreaHeight / 2f

                        reorderableLazyColumnState.onDragStart(key, dragAreaCenterOffsetY)
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDragStart()
                },
                onDragEnd = {
                    reorderableLazyColumnState.onDragEnd()
                    onDragEnd()
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    reorderableLazyColumnState.onDrag(dragAmount)
                },
            )
        }
}

fun Modifier.detectDragGesturesAfterLongPress(
    key: Any?,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    onDragStart: (offset: Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var dragInteractionStart by remember { mutableStateOf<DragInteraction.Start?>(null) }
    var dragStarted by remember { mutableStateOf(false) }

    DisposableEffect(key) {
        onDispose {
            if (dragStarted) {
                dragInteractionStart?.also {
                    coroutineScope.launch {
                        interactionSource?.emit(DragInteraction.Cancel(it))
                    }
                }

                if (dragStarted) {
                    onDragEnd()
                }

                dragStarted = false
            }
        }
    }

    pointerInput(key, enabled) {
        if (!enabled) {
            return@pointerInput
        }

        detectDragGesturesAfterLongPress(
            onDragStart = { offset: Offset ->
                dragStarted = true
                dragInteractionStart =
                    DragInteraction.Start().also {
                        coroutineScope.launch {
                            interactionSource?.emit(it)
                        }
                    }
                onDragStart(offset)
            },
            onDragEnd = {
                dragInteractionStart?.also {
                    coroutineScope.launch {
                        interactionSource?.emit(DragInteraction.Stop(it))
                    }
                }

                if (dragStarted) {
                    onDragEnd()
                }

                dragStarted = false
            },
            onDragCancel = {
                dragInteractionStart?.also {
                    coroutineScope.launch {
                        interactionSource?.emit(DragInteraction.Cancel(it))
                    }
                }

                if (dragStarted) {
                    onDragEnd()
                }

                dragStarted = false
            },
            onDrag = onDrag,
        )
    }
}
