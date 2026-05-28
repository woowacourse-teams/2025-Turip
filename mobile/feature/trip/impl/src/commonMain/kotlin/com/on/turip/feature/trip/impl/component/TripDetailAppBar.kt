package com.on.turip.feature.trip.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun TripDetailAppBar(
    isBookmarked: Boolean,
    isLoading: Boolean,
    isError: Boolean,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        contentColor = TuripTheme.colors.white,
        containerColor = TuripTheme.colors.primary,
        modifier = modifier,
        start = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "뒤로가기",
                modifier = Modifier.clickable(onClick = onBackClick),
            )
        },
        end = {
            if (!isError && !isLoading) {
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(48.dp).offset(x = 10.dp),
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "북마크 해제" else "북마크",
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        },
    )
}
