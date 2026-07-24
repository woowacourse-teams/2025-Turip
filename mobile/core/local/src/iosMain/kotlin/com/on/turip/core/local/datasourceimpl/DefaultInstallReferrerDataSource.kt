package com.on.turip.core.local.datasourceimpl

import com.on.turip.core.data.datasource.InstallReferrerDataSource

class DefaultInstallReferrerDataSource : InstallReferrerDataSource {
    override suspend fun getInstallReferrer(): Result<String?> = Result.success(null)
}
