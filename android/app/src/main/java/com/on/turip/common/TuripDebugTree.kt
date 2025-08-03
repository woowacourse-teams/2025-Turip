package com.on.turip.common

import timber.log.Timber

class TuripDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "$DEBUG_LOG_PREFIX ${element.className}:${element.lineNumber}#${element.methodName}"

    companion object {
        private const val DEBUG_LOG_PREFIX = "moongjenut"
    }
}
