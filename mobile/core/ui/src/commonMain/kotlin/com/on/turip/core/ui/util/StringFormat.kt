package com.on.turip.core.ui.util

private val formatTokenRegex = Regex("%(?:\\d+\\$)?[sd]")

fun String.formatResource(vararg args: Any): String {
    var index = 0
    return formatTokenRegex.replace(this) {
        args.getOrNull(index++)?.toString() ?: it.value
    }
}
