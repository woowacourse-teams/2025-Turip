package com.on.turip.feature.trip.impl.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreatorInformation(
    thumbnailUrl: String,
    name: String,
    modifier: Modifier = Modifier,
) {
    val headerDiagonalHeight = 80.dp
    val thumbnailOverlap = 60.dp

    Column(modifier = modifier) {
        Box {
            DiagonalBackground(
                modifier =
                    Modifier
                        .height(headerDiagonalHeight)
                        .fillMaxWidth(),
            )

            CreatorDetail(
                thumbnailUrl = thumbnailUrl,
                name = name,
                modifier =
                    Modifier.padding(
                        start = TuripTheme.spacing.extraLarge,
                        end = TuripTheme.spacing.extraLarge,
                        top = headerDiagonalHeight - thumbnailOverlap,
                    ),
            )
        }
        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraLarge))
    }
}

@Composable
private fun DiagonalBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = TuripTheme.colors.primary,
) {
    Canvas(
        modifier =
            modifier
                .drawWithCache {
                    val path =
                        Path().apply {
                            // 좌상단
                            moveTo(0f, 0f)

                            // 선 시작점
                            lineTo(
                                size.width * 0f,
                                size.height * 0.9f,
                            )

                            // 꺾인 선
                            lineTo(
                                size.width * 0.53f,
                                size.height * 0.5f,
                            )
                            lineTo(
                                size.width * 0.54f,
                                size.height * 1f,
                            )
                            lineTo(
                                size.width * 1f,
                                size.height * 0.7f,
                            )

                            // 우상단
                            lineTo(size.width, 0f)

                            close()
                        }

                    onDrawBehind {
                        drawPath(
                            path = path,
                            color = backgroundColor,
                        )
                    }
                },
    ) {
    }
}

@Composable
private fun CreatorDetail(
    thumbnailUrl: String,
    name: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        modifier = modifier,
    ) {
        CreatorThumbnail(imageUrl = thumbnailUrl)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_youtuber_badge),
                contentDescription = null,
            )
            Text(
                text = name,
                style = TuripTheme.typography.title3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CreatorThumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .size(84.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
        placeholder = painterResource(Res.drawable.bg_image_placeholder),
        error = painterResource(Res.drawable.ic_sorry),
    )
}

@Preview(showBackground = true)
@Composable
private fun CreatorInformationPreview() {
    TuripTheme {
        CreatorInformation(
            thumbnailUrl = "",
            name = "여행하는 튜립팀 ABCDEFG",
        )
    }
}
