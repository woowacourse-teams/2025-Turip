package com.on.turip.core.navigation

/**
 * 화면 전환 애니메이션 길이(ms).
 *
 * 전환 자체는 앱 엔트리의 NavDisplay 가 그리지만, 전환이 끝난 뒤에 무언가를 시작해야 하는 화면
 * (예: 진입하자마자 시트를 올리는 화면)도 이 값을 기준으로 기다린다. 한쪽만 바꾸면 그 화면들이 전환과 겹쳐 끊긴다.
 */
const val SCREEN_TRANSITION_DURATION_MILLIS: Int = 300
