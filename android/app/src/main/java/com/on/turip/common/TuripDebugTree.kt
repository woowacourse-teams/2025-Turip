package com.on.turip.common

import timber.log.Timber

class TuripDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? =
        "moongjenut ${element.className}:${element.lineNumber}#${element.methodName}"
}
