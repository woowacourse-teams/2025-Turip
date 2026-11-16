package com.on.turip.feature.randomtravel.impl.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

@Composable
internal actual fun rememberReduceMotionEnabled(): Boolean = remember { UIAccessibilityIsReduceMotionEnabled() }
