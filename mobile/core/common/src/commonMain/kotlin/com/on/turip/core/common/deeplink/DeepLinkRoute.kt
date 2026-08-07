package com.on.turip.core.common.deeplink

/**
 * 실제로 실행(네비게이션)이 가능한 딥링크 라우트만 표현한다.
 *
 * [DeepLinkEvent]는 이 타입만 보관하므로, 소비 측에서 강제 캐스팅 없이 안전하게 분기할 수 있다.
 * (Unsupported는 [DeepLinkParseResult] 단계에서 걸러져 이벤트가 되지 않는다.)
 */
sealed interface ActionableDeepLinkRoute {
    /** 동일 딥링크 판별용 키. 라우팅 결과에서 파생하며 원본 토큰/URL을 노출하지 않는다. */
    val dedupeKey: String

    data class Invitation(
        val token: String,
    ) : ActionableDeepLinkRoute {
        // 호출마다 재계산하지 않도록 val로 1회 계산. 토큰은 fingerprint로만 노출.
        override val dedupeKey: String = "invitation:${sha256Fingerprint(token)}"

        // data class 자동 toString()이 raw token을 로그에 노출하지 않도록 redacted.
        override fun toString(): String = "Invitation(token=REDACTED)"
    }
}

/** 지원하지 않는 딥링크의 사유. 원본 URL을 보관하지 않는다. */
enum class UnsupportedReason {
    /** token 등 필수 파라미터가 없음 */
    MISSING_TOKEN,

    /** 인식할 수 없는 형식/스킴 */
    MALFORMED,
}

/**
 * 딥링크 파싱 결과. 파싱은 [DeepLinkStore.offer] 시점에 1회만 수행한다.
 */
sealed interface DeepLinkParseResult {
    data class Supported(
        val route: ActionableDeepLinkRoute,
    ) : DeepLinkParseResult

    data class Unsupported(
        val reason: UnsupportedReason,
    ) : DeepLinkParseResult
}
