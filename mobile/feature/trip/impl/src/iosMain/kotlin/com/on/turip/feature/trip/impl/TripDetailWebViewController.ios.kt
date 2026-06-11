package com.on.turip.feature.trip.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIInterfaceOrientationMaskLandscape
import platform.UIKit.UIInterfaceOrientationMaskPortrait
import platform.UIKit.UIWindowSceneGeometryPreferencesIOS
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@Stable
actual class TripDetailWebViewController internal constructor(
    internal val webView: WKWebView,
    private val baseUrl: String,
    private val navigateToWebViewUrl: (webUrl: String) -> Unit,
    private val userContentController: WKUserContentController,
) {
    private var fullScreenState by mutableStateOf(false)
    actual val isFullScreen: Boolean get() = fullScreenState
    private var loadingState by mutableStateOf(false)
    actual val isLoading: Boolean get() = loadingState
    private var errorState by mutableStateOf(false)
    actual val isError: Boolean get() = errorState
    private var playbackRestoringState by mutableStateOf(false)
    actual val isPlaybackRestoring: Boolean get() = playbackRestoringState

    private var currentUrl: String = ""
    private var currentVideoId: String = ""

    actual fun loadVideo(
        url: String,
        initialSecond: Int,
        onTimeUpdate: (time: Int) -> Unit,
    ) {
        if (url.isEmpty() || url == currentUrl) return
        currentUrl = url
        errorState = false
        loadingState = true
        playbackRestoringState = initialSecond > 0

        val baseNsUrl = NSURL.URLWithString(baseUrl) ?: run {
            errorState = true
            loadingState = false
            playbackRestoringState = false
            return
        }

        currentVideoId = extractVideoId(url)
        webView.loadHTMLString(
            string = buildIframeHtml(
                videoId = currentVideoId,
                initialSecond = initialSecond.coerceAtLeast(0),
                origin = baseUrl.trimEnd('/'),
            ),
            baseURL = baseNsUrl,
        )
        onTimeUpdate(initialSecond.coerceAtLeast(0))
        loadingState = false
        playbackRestoringState = false
    }

    actual fun seekTo(seconds: Int) {
        val safeSeconds = seconds.coerceAtLeast(0)
        webView.evaluateJavaScript(
            "if (typeof player !== 'undefined' && player.seekTo) { player.seekTo($safeSeconds, true); }",
            completionHandler = null,
        )
    }

    actual fun restoreTo(seconds: Int) {
        if (seconds <= 0) return
        seekTo(seconds)
    }

    actual fun canHandleBack(): Boolean = webView.canGoBack() || isFullScreen

    actual fun handleBack() {
        when {
            isFullScreen -> setFullScreen(false)
            webView.canGoBack() -> webView.goBack()
        }
    }

    actual fun clear() {
        userContentController.removeScriptMessageHandlerForName(FULLSCREEN_MESSAGE_HANDLER_NAME)
        webView.stopLoading()
        webView.loadHTMLString("", baseURL = null)
        currentVideoId = ""
        fullScreenState = false
    }

    internal fun openExternal(url: String) {
        navigateToWebViewUrl(url)
    }

    internal fun setFullScreen(isFullScreen: Boolean) {
        fullScreenState = isFullScreen
    }
}

@Composable
@OptIn(ExperimentalForeignApi::class)
actual fun rememberTripDetailWebViewController(
    baseUrl: String,
    navigateToWebViewUrl: (webUrl: String) -> Unit,
): TripDetailWebViewController =
    remember(baseUrl) {
        var controller: TripDetailWebViewController? = null
        val userContentController =
            WKUserContentController().apply {
                addScriptMessageHandler(
                    scriptMessageHandler =
                        FullScreenScriptMessageHandler { isFullScreen ->
                            controller?.setFullScreen(isFullScreen)
                        },
                    name = FULLSCREEN_MESSAGE_HANDLER_NAME,
                )
            }
        val configuration =
            WKWebViewConfiguration().apply {
                allowsInlineMediaPlayback = true
                mediaTypesRequiringUserActionForPlayback = 0u
                this.userContentController = userContentController
            }
        controller = TripDetailWebViewController(
            webView = WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = configuration),
            baseUrl = baseUrl,
            navigateToWebViewUrl = navigateToWebViewUrl,
            userContentController = userContentController,
        )
        controller
    }

@Composable
actual fun HandleFullScreenWindowLaunchedEffect(isFullScreen: Boolean) {
    DisposableEffect(Unit) {
        onDispose {
            setDeviceOrientation(UIDevice_ORIENTATION_PORTRAIT)
        }
    }

    LaunchedEffect(isFullScreen) {
        setDeviceOrientation(
            if (isFullScreen) {
                UIDevice_ORIENTATION_LANDSCAPE_LEFT
            } else {
                UIDevice_ORIENTATION_PORTRAIT
            },
        )
    }
}

private fun extractVideoId(videoUrl: String): String {
    val patterns =
        listOf(
            Regex("youtu\\.be/([^?&/]+)"),
            Regex("[?&]v=([^?&/]+)"),
            Regex("/embed/([^?&/]+)"),
            Regex("/shorts/([^?&/]+)"),
        )
    return patterns.firstNotNullOfOrNull { pattern ->
        pattern.find(videoUrl)?.groupValues?.getOrNull(1)
    } ?: videoUrl.substringAfterLast('/').substringBefore('?').substringBefore('&')
}

private fun setDeviceOrientation(orientation: ULong) {
    val windowScene = UIApplication.sharedApplication.keyWindow?.windowScene ?: return
    val preferences =
        UIWindowSceneGeometryPreferencesIOS(
            interfaceOrientations = orientation,
        )
    windowScene.requestGeometryUpdateWithPreferences(
        geometryPreferences = preferences,
        errorHandler = null,
    )
}

private const val UIDevice_ORIENTATION_PORTRAIT = UIInterfaceOrientationMaskPortrait
private const val UIDevice_ORIENTATION_LANDSCAPE_LEFT = UIInterfaceOrientationMaskLandscape
private const val FULLSCREEN_MESSAGE_HANDLER_NAME = "turipFullscreen"

private class FullScreenScriptMessageHandler(
    private val onFullScreenChanged: (Boolean) -> Unit,
) : NSObject(), WKScriptMessageHandlerProtocol {
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage,
    ) {
        onFullScreenChanged(didReceiveScriptMessage.body == "enter")
    }
}

private fun buildIframeHtml(
    videoId: String,
    initialSecond: Int,
    origin: String,
): String =
    """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport"
              content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">
        <style> html, body { margin: 0; padding: 0; width: 100%; height: 100%; }
            #player { width: 100%; height: 100%; }
        </style>
    </head>
    <body>
    <div id="player"></div>
    <script>
        var tag = document.createElement('script');
        tag.src = "https://www.youtube.com/iframe_api";
        var firstScriptTag = document.getElementsByTagName('script')[0];
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

        var player;
        function onYouTubeIframeAPIReady() {
            player = new YT.Player('player', {
                width: window.innerWidth,
                height: window.innerHeight,
                videoId: "$videoId",
                playerVars: {
                    'controls': 1,
                    'playsinline': 1,
                    'autoplay': 1,
                    'start': $initialSecond,
                    'origin': "$origin"
                },
                events: {
                    'onReady': onPlayerReady,
                    'onError': onPlayerError
                }
            });
        }
        function seekToSecond(seconds) {
            if(player && typeof player.seekTo == 'function') {
                player.seekTo(seconds, true);
            }
        }

        function onPlayerReady(event) {
            const iframe = event.target.getIframe();
            iframe.setAttribute("allow", "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen");
            iframe.setAttribute("allowfullscreen", "true");
            iframe.setAttribute("webkitallowfullscreen", "true");
            iframe.setAttribute("referrerpolicy", "strict-origin-when-cross-origin");
            bindFullscreenEvents(iframe);
            event.target.playVideo();
        }

        function onPlayerError(event) {
            window.webkit && window.webkit.messageHandlers && console.log(event.data);
        }

        function postFullscreenState(active) {
            if (window.webkit &&
                window.webkit.messageHandlers &&
                window.webkit.messageHandlers.$FULLSCREEN_MESSAGE_HANDLER_NAME) {
                window.webkit.messageHandlers.$FULLSCREEN_MESSAGE_HANDLER_NAME.postMessage(active ? "enter" : "exit");
            }
        }

        function isFullscreenActive() {
            return !!(
                document.fullscreenElement ||
                document.webkitFullscreenElement ||
                document.webkitCurrentFullScreenElement
            );
        }

        function bindFullscreenEvents(iframe) {
            document.addEventListener("fullscreenchange", function() {
                postFullscreenState(isFullscreenActive());
            });
            document.addEventListener("webkitfullscreenchange", function() {
                postFullscreenState(isFullscreenActive());
            });
            iframe.addEventListener("webkitbeginfullscreen", function() {
                postFullscreenState(true);
            });
            iframe.addEventListener("webkitendfullscreen", function() {
                postFullscreenState(false);
            });
        }
    </script>
    </body>
    </html>
    """.trimIndent()
