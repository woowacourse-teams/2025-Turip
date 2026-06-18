package com.on.buildlogic.convention.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.compose.ComposeExtension

val Project.compose: ComposeExtension
    get() = extensions["compose"] as ComposeExtension
