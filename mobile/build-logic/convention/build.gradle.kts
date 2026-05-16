plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("turip.convention.kmp.library") {
            id = "turip.convention.kmp.library"
            implementationClass = "com.on.buildlogic.convention.KmpLibraryConventionPlugin"
        }

        register("turip.convention.kmp.compose") {
            id = "turip.convention.kmp.compose"
            implementationClass = "com.on.buildlogic.convention.KmpComposeConventionPlugin"
        }

        register("turip.convention.kmp.application") {
            id = "turip.convention.kmp.application"
            implementationClass = "com.on.buildlogic.convention.KmpApplicationConventionPlugin"
        }

        register("turip.convention.kotlin.serialization") {
            id = "turip.convention.kotlin.serialization"
            implementationClass = "com.on.buildlogic.convention.KotlinSerializationConventionPlugin"
        }

        register("turip.convention.kotlin.feature.api") {
            id = "turip.convention.kotlin.feature.api"
            implementationClass = "com.on.buildlogic.convention.KmpFeatureApiConventionPlugin"
        }

        register("turip.convention.kotlin.feature.impl") {
            id = "turip.convention.kotlin.feature.impl"
            implementationClass = "com.on.buildlogic.convention.KmpFeatureImplConventionPlugin"
        }
    }
}
