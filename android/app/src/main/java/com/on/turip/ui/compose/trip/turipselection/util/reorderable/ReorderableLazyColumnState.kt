package com.on.turip.ui.compose.trip.turipselection.util.reorderable

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

/**
 * 스크롤 가능한 순서 변경을 위해 정의한 State
 */
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
        get() = draggingItemKey?.let { state.layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggingItemKey } }
    private val draggingItemIndex: Int?
        get() = draggingItemInfo?.index

    private val onMoveStateMutex: Mutex = Mutex()

    val isAnyItemDragging by derivedStateOf { draggingItemKey != null }

    private var draggingItemDraggedDeltaY by mutableFloatStateOf(0f)
    private var draggingItemInitialOffsetY by mutableFloatStateOf(0f)

    // visibleItemsInfo 가 onMove 이후에 즉시 업데이트 되지 않아서 드래그 아이템 위치 계산에 발생하는 문제를 보정하기 위한 변수들
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

    // 드래그 판단 시작 지점
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
                // 이전 드래그 아이템 위치를 먼저 snap
                previousDraggingItemOffsetY.snapTo(startOffsetY)
                // 0으로 애니메이션
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

    // 임계 영역 끝으로 갈수록 스크롤 속도 증가
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

                // 아이템 위치 SWAP 요청
                scope.(onMoveState.value)(draggingItem, targetItem)

                // SWAP 하며 레이아웃이 갱신되었을 때, 드래그 아이템이 있어야 할 위치 예측
                predictedDraggingItemOffsetY =
                    if (targetItem.index > draggingItem.index) {
                        (targetItem.offset + targetItem.size) - draggingItem.size
                    } else {
                        targetItem.offset
                    }.toFloat()

                withTimeout(1_000L) {
                    // 1.이동 전 상태 2.이동 후 상태 / 새로운 레이아웃 반영 완료 시점을 동기화하기 위함
                    layoutInfoFlow.take(2).collect()
                }

                oldDraggingItemIndex = null
                predictedDraggingItemOffsetY = null
            }
        } catch (e: CancellationException) {
            // withTimeout 발생해도 예외를 전파할 필요가 없으므로 무시
        }
    }

    private fun findTargetItem(
        draggingItemStartOffsetY: Float,
        draggingItemEndOffsetY: Float,
        items: List<LazyListItemInfo>,
        direction: Scroller.Direction,
        additionPredicate: (LazyListItemInfo) -> Boolean = { true },
    ): LazyListItemInfo? {
        val isTargetItem = { item: LazyListItemInfo ->
            val candidateCenterY = item.offset + item.size / 2f
            (candidateCenterY in draggingItemStartOffsetY..draggingItemEndOffsetY) &&
                additionPredicate(item)
        }

        val targetItem =
            when (direction) {
                Scroller.Direction.FORWARD -> items.find(isTargetItem)
                Scroller.Direction.BACKWARD -> items.findLast(isTargetItem)
            }
        return targetItem
    }
}
