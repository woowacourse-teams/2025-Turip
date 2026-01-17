package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun TripDetailAppBar(
    isError: Boolean,
    isContentFavorite: Boolean,
    onBackClick: () -> Unit,
    onContentFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentFavoriteIconRes =
        if (isContentFavorite) R.drawable.btn_bookmark_selected else R.drawable.btn_bookmark_normal

    TuripAppBar(
        contentColor = TuripTheme.colors.white,
        containerColor = TuripTheme.colors.primary,
        start = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.clickable(onClick = onBackClick),
            )
        },
        end = {
            if (!isError) {
                Icon(
                    painter = painterResource(contentFavoriteIconRes),
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = onContentFavoriteClick),
                )
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "컨텐츠 찜한 상태")
@Composable
private fun IsFavoriteTripAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = false,
            isContentFavorite = true,
            onBackClick = { },
            onContentFavoriteClick = { },
        )
    }
}

@Preview(showBackground = true, name = "컨텐츠 찜하지 않은 상태")
@Composable
private fun IsNotFavoriteTripAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = false,
            isContentFavorite = false,
            onBackClick = { },
            onContentFavoriteClick = { },
        )
    }
}

@Preview(showBackground = true, name = "화면에 에러 발생")
@Composable
private fun ErrorFavoriteTripAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = true,
            isContentFavorite = false,
            onBackClick = { },
            onContentFavoriteClick = { },
        )
    }
}
