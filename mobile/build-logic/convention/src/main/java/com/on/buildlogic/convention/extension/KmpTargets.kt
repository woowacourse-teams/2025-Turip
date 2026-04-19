package com.on.buildlogic.convention.extension

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureDialogTargets(
    jvmTarget: JvmTarget = JvmTarget.JVM_21,
) {
    androidTarget {
        compilerOptions { this.jvmTarget.set(jvmTarget) }
    }

    iosArm64()
    iosSimulatorArm64()
}
