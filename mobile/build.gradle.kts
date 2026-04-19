plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.ktlint) apply false
    alias(libs.plugins.ksp.gradle.plugin) apply false
    alias(libs.plugins.ktrofit) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.sentry.kmp) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true)
    }

    pluginManager.withPlugin("com.google.devtools.ksp") {
        val kspCommonMainMetadataTasks = tasks.matching { it.name == "kspCommonMainKotlinMetadata" }

        tasks
            .withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>()
            .matching { it.name.endsWith("OverCommonMainSourceSet") }
            .configureEach {
                dependsOn(kspCommonMainMetadataTasks)
            }
    }
}
