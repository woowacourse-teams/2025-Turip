package com.on.turip.feature.randomtravel.impl.platform

import androidx.compose.runtime.Composable

/**
 * OS의 "동작 줄이기(Reduce Motion)" 설정 여부.
 *
 * true 이면 슬롯 회전 연출을 생략하고 결과로 바로 전환한다.
 */
@Composable
internal expect fun rememberReduceMotionEnabled(): Boolean
