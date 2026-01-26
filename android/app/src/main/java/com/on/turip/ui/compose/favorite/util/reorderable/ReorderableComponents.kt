package com.on.turip.ui.compose.favorite.util.reorderable

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

/**
 * 자동 스크롤 시 한 번에 이동할 최대 스크롤 거리 비율
 *
 * viewport 높이에 곱해 실제 스크롤 픽셀 수를 계산한다.
 * 값이 클수록 드래그 중 스크롤 속도가 빨라진다.
 */
private const val SCROLL_AMOUNT_MULTIPLIER = 0.05f

/**
 * LazyColumn 아이템 재정렬을 위한 State
 *
 * - 드래그 시작/종료 이벤트 관리
 * - 드래그 위치에 따른 자동 스크롤 처리
 * - 아이템 간 순서 변경(onMove) 트리거
 *
 * @param lazyListState 대상 LazyColumn의 상태
 * @param scrollThreshold 자동 스크롤이 시작되는 상/하단 여유 영역
 * @param onDragStart 아이템 드래그 시작 시 호출
 * @param onDragEnd 아이템 드래그 종료 시 호출
 * @param onMove 드래그 중 아이템 순서 변경이 발생할 때 호출
 */
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

    // recomposition 이후에도 최신 callback을 사용하기 위해 rememberUpdatedState 사용
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

/**
 * 재정렬 가능한 아이템을 사용하기 위한 Wrapper
 * @param state 재정렬을 위해 선언한 State
 * @param key 아이템을 판별할 id 값
 *
 */
@Composable
fun LazyItemScope.ReorderableItem(
    state: ReorderableLazyColumnState,
    key: Long,
    modifier: Modifier = Modifier,
    animateItemModifier: Modifier = Modifier.animateItem(),
    content: @Composable ReorderableLazyColumnItemScope.(isDragging: Boolean) -> Unit,
) {
    val isDragging: Boolean by state.isDragging(key)
    val scale by animateFloatAsState(if (isDragging) 1.02f else 1f)

    // 아이템 상태에 따라 적용할 이동/애니메이션 modifier 결정
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
            }.onGloballyPositioned { itemPosition = it.positionInRoot().y }, // 아이템의 실제 화면상 Y 위치 측정
    ) {
        val itemScope: ReorderableLazyColumnItemScope =
            remember(state, key) {
                ReorderableLazyColumnItemScope(
                    reorderableLazyColumnState = state,
                    key = key,
                    itemPositionYProvider = { itemPosition },
                )
            }

        itemScope.content(isDragging)
    }
}
