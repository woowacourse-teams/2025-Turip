package com.on.turip.core.common

enum class Platform {
    Android,
    IOS,
}

expect val currentPlatform: Platform
