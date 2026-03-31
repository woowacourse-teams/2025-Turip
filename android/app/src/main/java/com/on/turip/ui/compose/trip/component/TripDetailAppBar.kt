package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun TripDetailAppBar(
    isError: Boolean,
    isLoading: Boolean,
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookmarkIconRes =
        if (isBookmarked) R.drawable.btn_bookmark_selected else R.drawable.btn_bookmark_normal

    TuripAppBar(
        contentColor = TuripTheme.colors.white,
        containerColor = TuripTheme.colors.primary,
        start = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.all_back_description),
                modifier = Modifier.clickable(onClick = onBackClick),
            )
        },
        end = {
            if (!isError && !isLoading) {
                Icon(
                    painter = painterResource(bookmarkIconRes),
                    contentDescription = stringResource(R.string.trip_detail_bookmark_description),
                    modifier =
                        Modifier
                            .clickable(onClick = onBookmarkClick)
                            .size(20.dp),
                )
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "컨텐츠 북마크 선택 상태")
@Composable
private fun IsBookmarkedTripDetailAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = false,
            isLoading = false,
            isBookmarked = true,
            onBackClick = { },
            onBookmarkClick = { },
        )
    }
}

@Preview(showBackground = true, name = "컨텐츠 북마크 해제 상태")
@Composable
private fun IsNotBookmarkedTripDetailAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = false,
            isBookmarked = false,
            onBackClick = { },
            onBookmarkClick = { },
            isLoading = false,
        )
    }
}

@Preview(showBackground = true, name = "화면에 에러 발생")
@Composable
private fun ErrorTripDetailAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = true,
            isBookmarked = false,
            onBackClick = { },
            onBookmarkClick = { },
            isLoading = true,
        )
    }
}
