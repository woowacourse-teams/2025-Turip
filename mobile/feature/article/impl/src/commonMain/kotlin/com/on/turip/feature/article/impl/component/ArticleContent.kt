package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.component.TuripMarkdown
import com.on.turip.core.designsystem.theme.TuripTheme

/**
 * 아티클 본문. 본문 좌우 여백은 20dp로, 홈 카드(16dp)와 일부러 다르게 둔다.
 */
@Composable
fun ArticleContent(
    content: String,
    modifier: Modifier = Modifier,
) {
    TuripMarkdown(
        content = content,
        modifier = modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
    )
}

@Preview(showBackground = true)
@Composable
private fun ArticleContentPreview() {
    TuripTheme {
        ArticleContent(
            content = "전주 한옥마을은 **조선시대**부터 이어진 전통 가옥이 밀집한 곳입니다.",
        )
    }
}
