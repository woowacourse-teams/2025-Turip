package com.on.turip.core.network.datasource

interface InstallReferrerDataSource {
    /**
     * Play Install Referrer 값을 조회한다.
     *
     * 반환값 규칙:
     * - `Result.success(referrer)`: referrer 문자열을 정상적으로 조회한 경우
     * - `Result.success(null)`: 기기/환경 특성으로 referrer 조회가 영구적으로 불가능한 경우 (`FEATURE_NOT_SUPPORTED`, `DEVELOPER_ERROR`, `PERMISSION_ERROR`)
     * - `Result.failure(exception)`: 일시적인 장애 또는 예외로 이번 실행에서 조회에 실패한 경우 (`SERVICE_UNAVAILABLE`, 서비스 연결 끊김, 타임아웃, 기타 예외)
     */
    suspend fun getInstallReferrer(): Result<String?>
}
