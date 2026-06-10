package com.on.turip.feature.trip.impl.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_play_button
import com.on.turip.core.designsystem.generated.resources.trip_detail_video_error
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContentVideo(
    onErrorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ContentVideoContent(
        isLoading = false,
        isError = false,
        onErrorClick = onErrorClick,
        videoContent = { Video() },
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
private fun Video() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TuripTheme.colors.black),
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
                text = stringResource(Res.string.trip_detail_video_error),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.white,
                textAlign = TextAlign.Center,
            )
            Icon(
                painter = painterResource(Res.drawable.ic_play_button),
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
            videoContent = { Video() },
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
            videoContent = { Video() },
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
