package com.on.turip.feature.turipdetail.impl.util.reorderable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope

private const val SCROLL_AMOUNT_MULTIPLIER = 0.05f

@Composable
fun rememberReorderableLazyColumnState(
    lazyListState: LazyListState,
    scrollThreshold: Dp = 50.dp,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onMove: suspend CoroutineScope.(from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
): ReorderableLazyColumnState {
    val scroller: Scroller =
        rememberScroller(
            scrollableState = lazyListState,
            scrollDistanceProvider = { lazyListState.layoutInfo.viewportSize.height * SCROLL_AMOUNT_MULTIPLIER },
        )
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { scrollThreshold.toPx() }

    val scope = rememberCoroutineScope()

    val onDragStartState = rememberUpdatedState(onDragStart)
    val onDragEndState = rememberUpdatedState(onDragEnd)
    val onMoveState = rememberUpdatedState(onMove)

    return remember(scope, lazyListState, scrollThreshold, scroller) {
        ReorderableLazyColumnState(
            state = lazyListState,
            scope = scope,
            onDragStartState = onDragStartState,
            onDragEndState = onDragEndState,
            onMoveState = onMoveState,
            scrollThreshold = scrollThresholdPx,
            scroller = scroller,
        )
    }
}

@Composable
fun LazyItemScope.ReorderableItem(
    state: ReorderableLazyColumnState,
    key: Long,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    animateItemModifier: Modifier = Modifier.animateItem(),
    content: @Composable ReorderableLazyColumnItemScope.(isDragging: Boolean) -> Unit,
) {
    val isDragging: Boolean by state.isDragging(key)
    val scale by animateFloatAsState(if (isDragging) 1.02f else 1f)

    val offsetModifier =
        if (isDragging) {
            Modifier
                .zIndex(1f)
                .graphicsLayer { translationY = state.draggingItemOffsetY }
        } else if (key == state.previousDraggingItemKey) {
            Modifier
                .zIndex(1f)
                .graphicsLayer { translationY = state.previousDraggingItemOffsetY.value }
        } else {
            animateItemModifier
        }

    var itemPosition by remember { mutableFloatStateOf(0f) }

    Box(
        modifier
            .then(offsetModifier)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }.onGloballyPositioned { itemPosition = it.positionInRoot().y },
    ) {
        val itemScope: ReorderableLazyColumnItemScope =
            remember(state, key, enabled) {
                ReorderableLazyColumnItemScope(
                    reorderableLazyColumnState = state,
                    key = key,
                    itemPositionYProvider = { itemPosition },
                    enabled = enabled,
                )
            }

        itemScope.content(isDragging)
    }
}
