package com.on.turip.ui.compose.trip.component

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.TripDetailWebViewController

@Composable
fun ContentVideo(
    onErrorClick: () -> Unit,
    webViewController: TripDetailWebViewController,
    modifier: Modifier = Modifier,
) {
    ContentVideoContent(
        isLoading = webViewController.isLoading,
        isError = webViewController.isError,
        onErrorClick = onErrorClick,
        videoContent = { Video(webViewController) },
        modifier = modifier,
    )
}

@Composable
private fun ContentVideoContent(
    isLoading: Boolean,
    isError: Boolean,
    onErrorClick: () -> Unit,
    modifier: Modifier = Modifier,
    videoContent: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clipToBounds(),
    ) {
        if (!isError) {
            videoContent()
        } else {
            VideoError(
                onClick = onErrorClick,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(TuripTheme.colors.black),
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                color = TuripTheme.colors.white,
            )
        }
    }
}

@Composable
private fun Video(webViewController: TripDetailWebViewController) {
    AndroidView(
        factory = {
            webViewController.webView.apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        update = { view ->
            view.visibility =
                if (webViewController.isFullScreen) View.INVISIBLE else View.VISIBLE
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun VideoError(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.trip_detail_video_error),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.white,
                textAlign = TextAlign.Center,
            )
            Icon(
                painter = painterResource(R.drawable.ic_play_button),
                contentDescription = null,
                tint = TuripTheme.colors.gray03,
            )
        }
    }
}

@Preview(showBackground = true, name = "재생 중 상태")
@Composable
private fun ContentVideoPlayingPreview() {
    TuripTheme {
        ContentVideoContent(
            isLoading = false,
            isError = false,
            onErrorClick = {},
            videoContent = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(TuripTheme.colors.black),
                ) {
                    Text(
                        text = "유튜브 영상 재생 되는 부분",
                        color = TuripTheme.colors.white,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true, name = "로딩 중 상태")
@Composable
private fun ContentVideoLoadingPreview() {
    TuripTheme {
        ContentVideoContent(
            isLoading = true,
            isError = false,
            onErrorClick = {},
            videoContent = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(TuripTheme.colors.black),
                )
            },
        )
    }
}

@Preview(showBackground = true, name = "에러 상태")
@Composable
private fun ContentVideoErrorPreview() {
    TuripTheme {
        ContentVideoContent(
            isLoading = false,
            isError = true,
            onErrorClick = {},
            videoContent = {},
        )
    }
}
