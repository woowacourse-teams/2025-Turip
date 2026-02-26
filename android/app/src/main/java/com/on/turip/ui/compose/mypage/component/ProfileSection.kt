package com.on.turip.ui.compose.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.on.turip.ui.compose.mypage.MyPageSectionState
import com.on.turip.ui.compose.mypage.model.ProfileModel

@Composable
fun ProfileSection(
    state: MyPageSectionState<ProfileModel>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        val imageUrl = (state as? MyPageSectionState.Success)?.data?.imageUrl
        ProfileImage(
            imageUrl = imageUrl,
            modifier = Modifier.size(84.dp),
        )

        ProfileRightContent(
            state = state,
            onRetry = onRetry,
            modifier = Modifier.weight(1f),
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
                ).clip(shape)
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
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.my_page_profile_image_description),
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
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.bg_image_placeholder),
                error = painterResource(R.drawable.ic_sorry),
            )
        }
    }
}

@Composable
private fun ProfileRightContent(
    state: MyPageSectionState<ProfileModel>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        when (state) {
            MyPageSectionState.Loading -> {}

            MyPageSectionState.Error -> {
                ProfileError(onRetry = onRetry)
            }

            is MyPageSectionState.Success -> {
                Text(
                    text = state.data.nickname,
                    textAlign = TextAlign.Start,
                    style = TuripTheme.typography.title1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ProfileError(onRetry: () -> Unit) {
    val shape = TuripTheme.shape.wideButton

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        modifier =
            Modifier
                .clip(shape)
                .border(
                    width = 1.dp,
                    shape = TuripTheme.shape.wideButton,
                    color = TuripTheme.colors.gray03,
                ).clickable(onClick = onRetry)
                .padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.small,
                ),
    ) {
        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = null,
            tint = TuripTheme.colors.gray03,
        )
        Text(
            text = stringResource(R.string.retry),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray03,
        )
    }
}

@Preview(showBackground = true, name = "에러")
@Composable
private fun ProfileErrorPreview() {
    TuripTheme {
        ProfileSection(
            state = MyPageSectionState.Error,
            onRetry = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.extraLarge),
        )
    }
}

@Preview(showBackground = true, name = "기본 프로필 이미지")
@Composable
private fun ProfileDefaultPreview() {
    TuripTheme {
        ProfileSection(
            state =
                MyPageSectionState.Success(
                    ProfileModel(
                        id = 1L,
                        nickname = "기본 프로필 기본 프로필 기본 프로필 기본 프로필 기본 프로필 이름이 길어지면",
                        imageUrl = null,
                    ),
                ),
            onRetry = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.extraLarge),
        )
    }
}
