package com.on.turip.core.navigation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack
import io.github.aakira.napier.Napier

class Navigator(
    val state: NavigationState,
) {
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
        log(action = "navigate to $key")
    }

    fun goBack() {
        state.currentSubStack.removeLastOrNull()
        log(action = "goBack")
    }

    fun replace(key: NavKey) {
        when (key) {
            in state.topLevelKeys -> goToTopLevel(key)
            else -> replaceToKey(key)
        }
        log(action = "replace to $key")
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            clear()
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }

    private fun replaceToKey(key: NavKey) {
        state.currentSubStack.apply {
            if (size > 1) removeLastOrNull()
            remove(key)
            add(key)
        }
    }

    private fun log(action: String) {
        Napier.i(
            tag = "Navigator",
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

    private fun NavBackStack<NavKey>.toLogString(): String =
        this.joinToString(prefix = "[", postfix = "]", separator = ", ")

    private fun Map<NavKey, NavBackStack<NavKey>>.toLogString(): String =
        entries.joinToString(separator = "\n") { (key, stack) ->
            "$key -> ${stack.toLogString()}"
        }
}
