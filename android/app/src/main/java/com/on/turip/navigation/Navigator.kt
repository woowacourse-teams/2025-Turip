package com.on.turip.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import timber.log.Timber

/**
 * Navigation3 기반 앱 내비게이션을 제어하는 상태 관리자입니다.
 *
 * 이 클래스는 `NavigationState`가 들고 있는 TopLevel 스택과 각 TopLevel의 서브 스택을
 * 일관된 규칙으로 조작합니다.
 * - TopLevel 키로 이동하면 해당 TopLevel의 루트 화면으로 전환합니다.
 * - 현재 TopLevel을 다시 선택하면 현재 TopLevel의 서브 스택을 루트만 남기고 정리합니다.
 * - TopLevel이 아닌 키는 현재 TopLevel의 서브 스택에서 push/replace 규칙으로 이동합니다.
 */
class Navigator(
    val state: NavigationState,
) {
    /**
     * 주어진 [key]로 이동합니다.
     *
     * - 현재 TopLevel 키와 동일하면 현재 서브 스택을 루트만 남기고 정리합니다.
     * - TopLevel 키면 해당 TopLevel로 전환합니다.
     * - 그 외 키면 현재 서브 스택 끝으로 이동합니다(기존 동일 키는 제거 후 재추가).
     */
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
        log(action = "navigate to $key")
    }

    /**
     * 현재 위치를 [key]로 치환해 이동합니다.
     *
     * - TopLevel 키면 해당 TopLevel로 전환합니다.
     * - 그 외 키면 현재 서브 스택의 마지막 화면을 제거한 뒤 [key]를 추가합니다.
     *   (동일 키가 이미 존재하면 중복을 제거하고 마지막으로 이동)
     */
    fun replace(key: NavKey) {
        when (key) {
            in state.topLevelKeys -> goToTopLevel(key)
            else -> replaceToKey(key)
        }
        log(action = "replace to $key")
    }

    /**
     * 한 단계 뒤로 이동합니다.
     *
     * 아래 경우에는 아무 동작도 하지 않습니다.
     * - 현재 키가 시작 키인 경우
     * - 현재 키가 현재 TopLevel 키인 경우(TopLevel 루트 화면)
     */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> Unit
            state.currentTopLevelKey -> Unit
            else -> state.currentSubStack.removeLastOrNull()
        }
        log(action = "goBack")
    }

    /**
     * 모든 내비게이션 스택을 초기화한 뒤 [key]로 이동합니다.
     *
     * - [key]가 TopLevel이면 해당 키를 TopLevel 스택의 시작으로 설정합니다.
     * - [key]가 TopLevel이 아니면 [parentTopLevelKey] (없거나 invalid면 startKey)로 초기화한 뒤 해당 서브 스택에 [key]를 추가합니다.
     *
     * 로그인 전환, 세션 전환처럼 기존 히스토리를 완전히 비워야 하는 시나리오에 사용합니다.
     */
    fun goWithAllClear(
        key: NavKey,
        parentTopLevelKey: NavKey = state.startKey,
    ) {
        clearAllStacks()
        when (key) {
            in state.topLevelKeys -> {
                state.topLevelStack.add(key)
                state.subStacks[key]?.add(key)
            }

            else -> {
                val topLevelKey: NavKey =
                    parentTopLevelKey.takeIf { it in state.topLevelKeys } ?: state.startKey
                state.topLevelStack.add(topLevelKey)
                val parentSubStack: NavBackStack<NavKey> =
                    state.subStacks[topLevelKey]
                        ?: error("Sub stack for $topLevelKey does not exist")
                parentSubStack.add(topLevelKey)
                parentSubStack.add(key)
            }
        }
        log(action = "goWithAllClear to $key")
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) clear() else remove(key)
            add(key)
        }
        val subStack = state.subStacks[key]
        if (subStack != null && subStack.isEmpty()) {
            subStack.add(key)
        }
    }

    private fun replaceToKey(key: NavKey) {
        state.currentSubStack.apply {
            if (size > 1) removeLastOrNull()
            remove(key)
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }

    private fun clearAllStacks() {
        state.topLevelStack.clear()
        state.subStacks.forEach { (_, stack) ->
            stack.clear()
        }
    }

    private fun log(action: String) {
        Timber.tag("Navigator").d(
            message =
                """
                ------------------------ Navigator --------------------------------
                [$action]
                currentTopLevelKey: ${state.currentTopLevelKey}
                currentKey: ${state.currentKey}

                topLevelStack: ${state.topLevelStack.toLogString()}
                subStacks:
                ${state.subStacks.toLogString()}
                currentSubStackSize: ${state.currentSubStack.size}
                ------------------------------------------------------------------------
                """.trimIndent(),
        )
    }

    private fun NavBackStack<NavKey>.toLogString(): String = this.joinToString(prefix = "[", postfix = "]", separator = ", ")

    private fun Map<NavKey, NavBackStack<NavKey>>.toLogString(): String =
        entries.joinToString(separator = "\n") { (key, stack) ->
            "$key -> ${stack.toLogString()}"
        }
}
