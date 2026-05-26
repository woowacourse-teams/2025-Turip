package com.jetbrains.kmpapp.di

import com.on.turip.BuildKonfig
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

fun initLogger() {
    if (BuildKonfig.IS_DEBUG) {
        Napier.base(DebugAntilog())
    } else {
        Napier.base(SentryAntilog())
    }
}

/**
 * Napier 로그를 Sentry로 포워딩하는 Antilog.
 *
 * - ERROR / ASSERT → Sentry 이벤트 생성 (대시보드 알림 발생)
 * - WARNING        → Breadcrumb로 기록 (이벤트 미생성, 다음 에러에 맥락으로 첨부)
 */
private class SentryAntilog : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?,
    ) {
        when (priority) {
            LogLevel.ERROR, LogLevel.ASSERT -> {
                if (throwable != null) {
                    Sentry.captureException(throwable) { scope ->
                        tag?.let { scope.setTag("napier_tag", it) }
                        message?.let { scope.setExtra("message", it) }
                        scope.level = SentryLevel.ERROR
                    }
                } else if (message != null) {
                    Sentry.captureMessage(message) { scope ->
                        tag?.let { scope.setTag("napier_tag", it) }
                        scope.level = SentryLevel.ERROR
                    }
                }
            }

            LogLevel.WARNING -> {
                val breadcrumb = Breadcrumb().apply {
                    this.message = message
                    level = SentryLevel.WARNING
                    category = tag
                }
                Sentry.addBreadcrumb(breadcrumb)
            }

            else -> Unit
        }
    }
}
