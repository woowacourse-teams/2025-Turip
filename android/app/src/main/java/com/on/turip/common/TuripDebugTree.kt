package com.on.turip.common

import timber.log.Timber

class TuripDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        val simpleClass: String = element.className.substringAfterLast('.')
        return "$DEBUG_LOG_PREFIX $simpleClass:${element.lineNumber}#${element.methodName}"
    }

    companion object {
        private const val DEBUG_LOG_PREFIX = "MJC"
    }
}
