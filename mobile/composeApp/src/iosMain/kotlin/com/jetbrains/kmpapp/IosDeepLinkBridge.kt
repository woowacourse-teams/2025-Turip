package com.on.turip

import com.on.turip.core.common.deeplink.DeepLinkEventSource
import com.on.turip.core.common.deeplink.DeepLinkStore

/**
 * iOS 딥링크 진입점과 Compose 소비자를 잇는 **프로세스 수명 싱글턴** 브리지.
 *
 * Store를 [MainViewController] 내부가 아니라 여기(프로세스 싱글턴)에 두는 것이 핵심이다.
 * 그래야 SwiftUI가 컴포지션보다 먼저 [emitIosDeepLink]를 호출해도(콜드 스타트) 값이 보관되어
 * 늦게 붙는 Collector에게 전달된다. Controller가 재생성돼도 같은 Store를 공유한다.
 */
internal object IosDeepLinkBridge {
    private val store = DeepLinkStore()

    val eventSource: DeepLinkEventSource get() = store

    fun offer(url: String?) = store.offer(url)
}

fun emitIosDeepLink(url: String) = IosDeepLinkBridge.offer(url)

fun iosDeepLinkEventSource(): DeepLinkEventSource = IosDeepLinkBridge.eventSource
