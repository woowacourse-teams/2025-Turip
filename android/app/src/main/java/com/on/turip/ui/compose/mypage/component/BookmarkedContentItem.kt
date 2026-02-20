package com.on.turip.ui.compose.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.login.util.noRippleClickable

@Composable
fun BookmarkedContentItem(
    onContentClick: () -> Unit,
    onRemoveBookmark: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.noRippleClickable { onContentClick() },
    ) {
        ContentThumbnail(imageUrl = "")

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        Text(
            text = "콘텐츠 제목 들어오는 곳",
            style = TuripTheme.typography.title2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraSmall),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        ContentInformation(onRemoveBookmark)
    }
}

@Composable
private fun ContentThumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val shape = TuripTheme.shape.container

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(shape)
                .background(TuripTheme.colors.primary),
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Composable
private fun ContentInformation(onRemoveBookmark: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = TuripTheme.spacing.extraSmall),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            Text(
                text = "크리에이터명 & 날짜",
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray03,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
            ) {
                ContentInfoItem(
                    text = "2박 3일",
                    iconPainterRes = R.drawable.ic_calendar,
                )
                ContentInfoItem(
                    text = "12개 장소",
                    iconPainterRes = R.drawable.ic_place,
                )
            }
        }
        IconButton(
            onClick = onRemoveBookmark,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.btn_bookmark_selected),
                contentDescription = null,
                tint = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun ContentInfoItem(
    text: String,
    @DrawableRes iconPainterRes: Int,
    modifier: Modifier = Modifier,
    textColor: Color = TuripTheme.colors.gray03,
    iconTint: Color = TuripTheme.colors.gray02,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(iconPainterRes),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
        )

        Text(
            text = text,
            style = TuripTheme.typography.info2,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkedContentItemPreview() {
    TuripTheme {
        BookmarkedContentItem(
            onContentClick = {},
            onRemoveBookmark = {},
            modifier = Modifier.width(280.dp),
        )
    }
}
