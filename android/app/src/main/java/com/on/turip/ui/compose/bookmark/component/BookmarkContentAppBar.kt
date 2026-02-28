package com.on.turip.ui.compose.bookmark.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun BookmarkContentAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        start = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.all_back_description),
                modifier = Modifier.clickable(onClick = onBackClick),
            )
        },
        center = {
            Text(
                text = stringResource(R.string.bookmark_content_title),
                style = TuripTheme.typography.title1,
            )
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkContentAppBarPreview() {
    TuripTheme {
        BookmarkContentAppBar(
            onBackClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
