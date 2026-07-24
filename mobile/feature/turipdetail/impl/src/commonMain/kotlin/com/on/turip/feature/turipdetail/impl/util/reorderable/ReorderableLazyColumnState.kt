package com.on.turip.feature.turipdetail.impl.util.reorderable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException

@Stable
class ReorderableLazyColumnState(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onDragStartState: State<() -> Unit>,
    private val onDragEndState: State<() -> Unit>,
    private val onMoveState: State<suspend CoroutineScope.(from: LazyListItemInfo, to: LazyListItemInfo) -> Unit>,
    private val scrollThreshold: Float,
    private val scroller: Scroller,
) {
    private var draggingItemKey by mutableStateOf<Long?>(null)
    private val draggingItemInfo: LazyListItemInfo?
        get() = draggingItemKey?.let { state.layoutInfo.visibleItemsInfo.firstOrNull { item -> item.key == draggingItemKey } }
    private val draggingItemIndex: Int?
        get() = draggingItemInfo?.index

    private val onMoveStateMutex: Mutex = Mutex()

    val isAnyItemDragging by derivedStateOf { draggingItemKey != null }

    private var draggingItemDraggedDeltaY by mutableFloatStateOf(0f)
    private var draggingItemInitialOffsetY by mutableFloatStateOf(0f)

    private var oldDraggingItemIndex by mutableStateOf<Int?>(null)
    private var predictedDraggingItemOffsetY by mutableStateOf<Float?>(null)

    val draggingItemOffsetY: Float
        get() =
            draggingItemInfo?.let {
                val offset =
                    if (it.index != oldDraggingItemIndex || oldDraggingItemIndex == null) {
                        oldDraggingItemIndex = null
                        predictedDraggingItemOffsetY = null
                        it.offset.toFloat()
                    } else {
                        predictedDraggingItemOffsetY ?: it.offset.toFloat()
                    }

                draggingItemDraggedDeltaY + draggingItemInitialOffsetY - offset
            } ?: 0f

    internal var previousDraggingItemKey by mutableStateOf<Long?>(null)
        private set
    internal var previousDraggingItemOffsetY = Animatable(0f)
        private set

    private var dragAnchorOffsetY = 0f

    fun isDragging(key: Long): State<Boolean> = derivedStateOf { key == draggingItemKey }

    internal suspend fun onDragStart(
        key: Long,
        dragAreaCenterOffsetY: Float,
    ) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> item.key == key }
            ?.also {
                if (it.offset < 0) state.animateScrollBy(it.offset.toFloat(), spring())

                draggingItemKey = key
                draggingItemInitialOffsetY = it.offset.toFloat()
                dragAnchorOffsetY = dragAreaCenterOffsetY
                onDragStartState.value()
            }
    }

    internal fun onDragEnd() {
        val previousDraggingItemInitialOffsetY = draggingItemInfo?.offset?.toFloat() ?: 0f

        if (draggingItemIndex != null) {
            previousDraggingItemKey = draggingItemKey
            val startOffsetY = draggingItemOffsetY

            scope.launch {
                previousDraggingItemOffsetY.snapTo(startOffsetY)
                previousDraggingItemOffsetY.animateTo(
                    0f,
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = 0.5f,
                    ),
                )
                previousDraggingItemKey = null
            }
        }

        draggingItemDraggedDeltaY = 0f
        draggingItemKey = null
        draggingItemInitialOffsetY = previousDraggingItemInitialOffsetY
        scroller.tryStop()
        oldDraggingItemIndex = null
        predictedDraggingItemOffsetY = null
        onDragEndState.value()
    }

    internal fun onDrag(dragAmount: Offset) {
        val offsetY = dragAmount.y
        draggingItemDraggedDeltaY += offsetY

        val draggingItem = draggingItemInfo ?: return

        val startOffset = draggingItem.offset + draggingItemOffsetY
        val endOffset = startOffset + draggingItem.size

        val dragAreaOffset = startOffset + dragAnchorOffsetY + state.layoutInfo.beforeContentPadding

        val distanceFromTop = dragAreaOffset.coerceAtLeast(0f)
        val distanceFromBottom =
            (state.layoutInfo.viewportSize.height - dragAreaOffset).coerceAtLeast(0f)

        val isScrollingStarted =
            if (distanceFromTop < scrollThreshold) {
                scroller.start(
                    direction = Scroller.Direction.BACKWARD,
                    speedMultiplier = getScrollSpeedMultiplier(distanceFromTop),
                    maxScrollDistanceProvider = {
                        draggingItemInfo?.let { state.layoutInfo.viewportSize.height - it.offset - 1f }
                            ?: 0f
                    },
                    onScroll = {
                        moveDraggingItemToEnd(Scroller.Direction.BACKWARD)
                    },
                )
            } else if (distanceFromBottom < scrollThreshold) {
                scroller.start(
                    direction = Scroller.Direction.FORWARD,
                    speedMultiplier = getScrollSpeedMultiplier(distanceFromBottom),
                    maxScrollDistanceProvider = {
                        draggingItemInfo?.let { it.offset - it.size - 1f } ?: 0f
                    },
                    onScroll = {
                        moveDraggingItemToEnd(Scroller.Direction.FORWARD)
                    },
                )
            } else {
                scroller.tryStop()
                false
            }

        if (!onMoveStateMutex.tryLock()) return
        if (!scroller.isScrolling && !isScrollingStarted) {
            val targetItem =
                findTargetItem(
                    draggingItemStartOffsetY = startOffset,
                    draggingItemEndOffsetY = endOffset,
                    items = state.layoutInfo.visibleItemsInfo,
                    direction = Scroller.Direction.FORWARD,
                ) { item -> item.index != draggingItem.index }

            if (targetItem != null) {
                scope.launch {
                    moveItem(draggingItem, targetItem)
                }
            }
        }
        onMoveStateMutex.unlock()
    }

    private fun getScrollSpeedMultiplier(distance: Float): Float =
        (1 - ((distance + scrollThreshold) / (scrollThreshold * 2)).coerceIn(0f, 1f)) * 10

    private suspend fun moveDraggingItemToEnd(direction: Scroller.Direction) {
        onMoveStateMutex.lock()

        val draggingItem = draggingItemInfo
        if (draggingItem == null) {
            onMoveStateMutex.unlock()
            return
        }

        val visibleItems = state.layoutInfo.visibleItemsInfo
        if (visibleItems.isEmpty()) {
            onMoveStateMutex.unlock()
            return
        }

        val isDraggingItemAtEnd =
            when (direction) {
                Scroller.Direction.FORWARD -> draggingItem.index == visibleItems.last().index
                Scroller.Direction.BACKWARD -> draggingItem.index == visibleItems.first().index
            }
        if (isDraggingItemAtEnd) {
            onMoveStateMutex.unlock()
            return
        }

        val itemsInContentArea =
            visibleItems.filter { item ->
                item.offset >= 0f && item.offset + item.size <= state.layoutInfo.viewportSize.height
            }

        val dragOffset = draggingItemOffsetY
        val startOffset = draggingItem.offset + dragOffset
        val endOffset = startOffset + draggingItem.size

        val targetItem =
            findTargetItem(
                draggingItemStartOffsetY = startOffset,
                draggingItemEndOffsetY = endOffset,
                items = itemsInContentArea,
                direction = direction.opposite,
            ) ?: run {
                when (direction) {
                    Scroller.Direction.FORWARD -> visibleItems.last()
                    Scroller.Direction.BACKWARD -> visibleItems.first()
                }
            }

        val isTargetDirectionCorrect =
            when (direction) {
                Scroller.Direction.FORWARD -> targetItem.index > draggingItem.index
                Scroller.Direction.BACKWARD -> targetItem.index < draggingItem.index
            }

        if (!isTargetDirectionCorrect) {
            onMoveStateMutex.unlock()
            return
        }

        val job = scope.launch { moveItem(draggingItem, targetItem) }

        onMoveStateMutex.unlock()
        job.join()
    }

    private val layoutInfoFlow = snapshotFlow { state.layoutInfo }

    private suspend fun moveItem(
        draggingItem: LazyListItemInfo,
        targetItem: LazyListItemInfo,
    ) {
        if (draggingItem.index == targetItem.index) return

        try {
            onMoveStateMutex.withLock {
                if (!isAnyItemDragging) return

                if (draggingItem.index == state.firstVisibleItemIndex || targetItem.index == state.firstVisibleItemIndex) {
                    state.requestScrollToItem(
                        state.firstVisibleItemIndex,
                        state.firstVisibleItemScrollOffset,
                    )
                }

                oldDraggingItemIndex = draggingItem.index

                scope.(onMoveState.value)(draggingItem, targetItem)

                predictedDraggingItemOffsetY =
                    if (targetItem.index > draggingItem.index) {
                        (targetItem.offset + targetItem.size) - draggingItem.size
                    } else {
                        targetItem.offset
                    }.toFloat()

                withTimeout(1_000L) {
                    layoutInfoFlow.take(2).collect()
                }

                oldDraggingItemIndex = null
                predictedDraggingItemOffsetY = null
            }
        } catch (e: CancellationException) {
            // Timeout while waiting for LazyColumn layout sync can be ignored.
        }
    }

    private fun findTargetItem(
        draggingItemStartOffsetY: Float,
        draggingItemEndOffsetY: Float,
        items: List<LazyListItemInfo>,
        direction: Scroller.Direction,
        additionPredicate: (lazyListItemInfo: LazyListItemInfo) -> Boolean = { true },
    ): LazyListItemInfo? {
        val isTargetItem = { item: LazyListItemInfo ->
            val candidateCenterY = item.offset + item.size / 2f
            (candidateCenterY in draggingItemStartOffsetY..draggingItemEndOffsetY) &&
                additionPredicate(item)
        }

        return when (direction) {
            Scroller.Direction.FORWARD -> items.find(isTargetItem)
            Scroller.Direction.BACKWARD -> items.findLast(isTargetItem)
        }
    }
}
