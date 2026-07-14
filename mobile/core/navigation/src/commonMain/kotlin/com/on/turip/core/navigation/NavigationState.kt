package com.on.turip.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration

@Composable
fun rememberNavigationState(
    startKey: NavKey,
    topLevelKeys: Set<NavKey>,
    initialEntryKey: NavKey?,
    configuration: SavedStateConfiguration,
): NavigationState {
    val topLevelStack = rememberNavBackStack(configuration = configuration, startKey)
    val subStacks =
        topLevelKeys.associateWith { key ->
            rememberNavBackStack(configuration = configuration, key)
        }

    return remember(startKey, topLevelKeys, initialEntryKey) {
        if (initialEntryKey != null) {
            clearAllStacks(topLevelStack = topLevelStack, subStacks = subStacks)
            when (initialEntryKey) {
                in subStacks.keys -> {
                    topLevelStack.add(initialEntryKey)
                }

                else -> {
                    topLevelStack.add(startKey)
                    val startSubStack: NavBackStack<NavKey> =
                        subStacks[startKey] ?: error("Sub stack for $startKey does not exist")
                    startSubStack.add(initialEntryKey)
                }
            }
        }

        NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }
}

/**
 * 모든 네비게이션 스택을 초기 상태로 되돌린다.
 *
 * - `topLevelStack`은 완전히 비운다.
 * - 각 서브 스택은 비운 뒤, 자신의 top-level 키 1개만 다시 넣어 기본 상태를 만든다.
 */
private fun clearAllStacks(
    topLevelStack: NavBackStack<NavKey>,
    subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    topLevelStack.clear()
    subStacks.forEach { (topLevelKey: NavKey, stack: NavBackStack<NavKey>) ->
        stack.clear()
        stack.add(topLevelKey)
    }
}

/**
 * @param startKey - 앱의 시작 라우트. 사용자는 이 라우트를 통해 앱을 종료하게 된다.
 * @param topLevelStack - 현재 선택된 최상위(Top-level) 라우트
 * @param subStacks - 각 최상위 라우트에 대응되는 백 스택들
 * @param currentTopLevelKey - 현재 선택된 최상위 라우트
 * @param currentKey - 현재 선택된 라우트
 * @param topLevelKeys - 최상위 라우트들
 * @param currentSubStack - 현재 선택된 최상위 라우트의 백 스택
 */
@Stable
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val isTopLevelKey
        get() = topLevelKeys.contains(currentKey)
    val currentTopLevelKey: NavKey by derivedStateOf {
        topLevelStack.lastOrNull() ?: error("등록된 Top키가 존재하지 않습니다.")
    }
    val currentKey: NavKey by derivedStateOf {
        currentSubStack.lastOrNull() ?: error("등록된 키가 존재하지 않습니다.")
    }

    val topLevelKeys
        get() = subStacks.keys

    val currentSubStack: NavBackStack<NavKey>
        get() =
            subStacks[currentTopLevelKey]
                ?: error("Sub stack for $currentTopLevelKey does not exist")
}

@Composable
fun NavigationState.toEntries(entryProvider: (navKey: NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
    val decoratedEntries =
        subStacks.mapValues { (_, stack) ->
            val decorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                    rememberViewModelStoreNavEntryDecorator<NavKey>(),
                )

            rememberDecoratedNavEntries(
                backStack = stack,
                entryProvider = entryProvider,
                entryDecorators = decorators,
            )
        }

    return topLevelStack.flatMap { decoratedEntries[it] ?: emptyList() }
}
