package com.on.turip.feature.bookmark.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun BookmarkContentTitleRow(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = TuripTheme.typography.title2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

@Preview(showBackground = true, name = "칩 포함")
@Composable
private fun BookmarkContentTitleRowWithTrailingPreview() {
    TuripTheme {
        BookmarkContentTitleRow(
            title = "콘텐츠 제목이 길어질 경우 말줄임 처리 확인용 텍스트입니다",
            trailing = {
                Text(
                    text = "테스트",
                    modifier =
                        Modifier
                            .background(TuripTheme.colors.primary)
                            .padding(TuripTheme.spacing.medium),
                )
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.large),
        )
    }
}

@Preview(showBackground = true, name = "타이틀만 존재")
@Composable
private fun BookmarkContentTitleRowWithoutTrailingPreview() {
    TuripTheme {
        BookmarkContentTitleRow(
            title = "타이틀만 있는 경우",
            trailing = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.large),
        )
    }
}
