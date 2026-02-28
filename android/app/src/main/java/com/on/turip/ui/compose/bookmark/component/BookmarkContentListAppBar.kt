package com.on.turip.ui.compose.bookmark.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun BookmarkContentListAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.all_back_description),
                )
            }
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
private fun BookmarkContentListAppBarPreview() {
    TuripTheme {
        BookmarkContentListAppBar(
            onBackClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
