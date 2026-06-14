package com.on.turip.feature.login.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import com.on.turip.feature.login.impl.createAppleSignInButton
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIControlEventTouchUpInside
import platform.darwin.NSObject

@Composable
@OptIn(ExperimentalForeignApi::class)
internal actual fun PlatformLoginButton(
    onLoginClick: () -> Unit,
    modifier: Modifier,
) {
    val currentOnLoginClick by rememberUpdatedState(onLoginClick)
    val action = remember { AppleButtonAction { currentOnLoginClick() } }
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.Black),
    ) {
        UIKitView(
            factory = {
                createAppleSignInButton().apply {
                    addTarget(
                        target = action,
                        action = NSSelectorFromString("onLoginClick"),
                        forControlEvents = UIControlEventTouchUpInside,
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private class AppleButtonAction(
    private val onLoginClick: () -> Unit,
) : NSObject() {
    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun onLoginClick() {
        onLoginClick.invoke()
    }
}
