// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ktlint) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id("com.google.firebase.crashlytics") version "3.0.5" apply false
    alias(libs.plugins.compose.compiler) apply false
}
buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}
