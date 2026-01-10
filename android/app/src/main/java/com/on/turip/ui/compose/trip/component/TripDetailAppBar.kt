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
    isContentFavorite: Boolean,
    onBackClick: () -> Unit,
    onContentFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            if (isContentFavorite) {
                Icon(
                    painter = painterResource(R.drawable.btn_bookmark_selected),
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = onContentFavoriteClick),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.btn_bookmark_normal),
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
            isContentFavorite = false,
            onBackClick = { },
            onContentFavoriteClick = { },
        )
    }
}
