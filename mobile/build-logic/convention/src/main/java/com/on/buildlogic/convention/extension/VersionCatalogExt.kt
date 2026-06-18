package com.on.buildlogic.convention.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.version(alias: String): String =
    findVersion(alias)
        .orElseThrow { IllegalArgumentException("Version catalog version alias '$alias' not found in libs.versions.toml") }
        .requiredVersion

internal fun VersionCatalog.versionInt(alias: String): Int = version(alias).toInt()

internal fun VersionCatalog.library(alias: String) = findLibrary(alias).get()
