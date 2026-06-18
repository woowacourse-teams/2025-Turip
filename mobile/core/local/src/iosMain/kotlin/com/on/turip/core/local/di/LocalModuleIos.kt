package com.on.turip.core.local.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.on.turip.core.data.datasource.InstallReferrerDataSource
import com.on.turip.core.local.database.TuripDatabase
import com.on.turip.core.local.datasourceimpl.DefaultInstallReferrerDataSource
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

val localModuleIos = module {
    single<TuripDatabase> {
        Room.databaseBuilder<TuripDatabase>(
            name = NSHomeDirectory() + "/turip.db",
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    single { get<TuripDatabase>().searchHistoryDao() }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val newPath = "${applicationSupportDirectory()}/datastore/turip_prefs.preferences_pb"
                migrateLegacyDataStoreIfNeeded(newPath)
                newPath.toPath()
            },
        )
    }

    single<InstallReferrerDataSource> {
        DefaultInstallReferrerDataSource()
    }
}

private fun applicationSupportDirectory(): String =
    NSSearchPathForDirectoriesInDomains(
        directory = NSApplicationSupportDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true,
    ).firstOrNull() as? String ?: NSHomeDirectory()

/**
 * DataStore 저장 경로를 NSHomeDirectory → Application Support로 옮기면서
 * 기존 사용자의 토큰/설정이 소실되지 않도록 구 경로 파일을 신 경로로 1회 이전한다.
 * 신 경로 파일이 이미 있으면(이전 완료 또는 신규 설치) 아무 것도 하지 않는다.
 */
@OptIn(ExperimentalForeignApi::class)
private fun migrateLegacyDataStoreIfNeeded(newPath: String) {
    val fileManager = NSFileManager.defaultManager
    val legacyPath = NSHomeDirectory() + "/datastore/turip_prefs.preferences_pb"

    if (!fileManager.fileExistsAtPath(legacyPath)) return
    if (fileManager.fileExistsAtPath(newPath)) return

    val newDirectory = "${applicationSupportDirectory()}/datastore"
    if (!fileManager.fileExistsAtPath(newDirectory)) {
        fileManager.createDirectoryAtPath(newDirectory, true, null, null)
    }

    val copied = fileManager.copyItemAtPath(legacyPath, newPath, null)
    if (copied) {
        fileManager.removeItemAtPath(legacyPath, null)
        Napier.d("iOS DataStore 레거시 경로 마이그레이션 완료")
    } else {
        Napier.e("iOS DataStore 레거시 경로 마이그레이션 실패. legacyPath=$legacyPath")
    }
}
