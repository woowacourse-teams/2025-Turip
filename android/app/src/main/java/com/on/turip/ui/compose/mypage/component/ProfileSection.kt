package com.on.turip.ui.compose.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun ProfileSection(
    nickname: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        ProfileImage(imageUrl = imageUrl, modifier = Modifier.size(84.dp))
        Text(
            text = nickname,
            textAlign = TextAlign.Start,
            style = TuripTheme.typography.title1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val shape = CircleShape

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    ambientColor = TuripTheme.colors.black,
                    spotColor = TuripTheme.colors.black,
                )
                .clip(shape)
                .background(TuripTheme.colors.white)
                .border(
                    width = 2.dp,
                    color = TuripTheme.colors.gray01,
                    shape = shape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                painter = painterResource(R.drawable.ic_profile_default),
                contentDescription = stringResource(R.string.my_page_profile_image_description),
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxSize(0.4f),
            )
        } else {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = stringResource(R.string.my_page_profile_image_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                placeholder = painterResource(R.drawable.bg_image_placeholder),
                error = painterResource(R.drawable.ic_sorry),
            )
        }
    }
}

@Preview(showBackground = true, name = "기본 프로필")
@Composable
private fun DefaultProfilePreview() {
    TuripTheme {
        ProfileSection(
            nickname = "기본 프로필",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.extraLarge),
            imageUrl = null,
        )
    }
}

@Preview(showBackground = true, name = "사진 프로필")
@Composable
private fun PhotoProfilePreview() {
    TuripTheme {
        ProfileSection(
            nickname = "사진 존재하는 프로필",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.extraLarge),
            imageUrl = "url",
        )
    }
}
