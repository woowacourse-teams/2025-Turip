package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun ContentBookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder

    val titleRes =
        if (isBookmarked) R.string.trip_detail_trip_bookmark_select else R.string.trip_detail_trip_bookmark_normal

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clip(TuripTheme.shape.wideButton)
                .clickable(onClick = onClick)
                .background(color = TuripTheme.colors.primary, shape = TuripTheme.shape.wideButton)
                .fillMaxWidth()
                .padding(vertical = TuripTheme.spacing.medium),
    ) {
        Icon(
            imageVector = icon,
            tint = TuripTheme.colors.white,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(TuripTheme.spacing.extraSmall))

        Text(
            text = stringResource(titleRes),
            color = TuripTheme.colors.white,
            style = TuripTheme.typography.title2,
        )
    }
}

@Preview(showBackground = true, name = "북마크 선택 상태")
@Composable
private fun ContentBookmarkButtonIsBookmarkedPreview() {
    TuripTheme {
        ContentBookmarkButton(
            isBookmarked = true,
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "북마크 해제 상태")
@Composable
private fun ContentBookmarkButtonIsNotBookmarkedPreview() {
    TuripTheme {
        ContentBookmarkButton(
            isBookmarked = false,
            onClick = {},
        )
    }
}
