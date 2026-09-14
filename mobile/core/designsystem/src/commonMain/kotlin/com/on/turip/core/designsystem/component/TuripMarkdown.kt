package com.on.turip.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.on.turip.core.designsystem.theme.TuripTheme

/**
 * 서버가 내려주는 마크다운 원문을 Turip 디자인 토큰으로 그린다.
 *
 * 이미지도 `![](url)` 형태로 본문 안에 들어 있어 [Coil3ImageTransformerImpl] 로 로딩한다.
 */
@Composable
fun TuripMarkdown(
    content: String,
    modifier: Modifier = Modifier,
) {
    Markdown(
        content = content,
        colors =
            markdownColor(
                text = TuripTheme.colors.gray05,
                codeBackground = TuripTheme.colors.container,
                inlineCodeBackground = TuripTheme.colors.container,
                dividerColor = TuripTheme.colors.border,
            ),
        typography =
            markdownTypography(
                text = TuripTheme.typography.body1,
                paragraph = TuripTheme.typography.body1,
                h1 = TuripTheme.typography.title1,
                h2 = TuripTheme.typography.title2,
                h3 = TuripTheme.typography.title3,
                quote = TuripTheme.typography.body1,
                ordered = TuripTheme.typography.body1,
                bullet = TuripTheme.typography.body1,
                list = TuripTheme.typography.body1,
                textLink =
                    TextLinkStyles(
                        style =
                            SpanStyle(
                                color = TuripTheme.colors.primary,
                                textDecoration = TextDecoration.Underline,
                            ),
                    ),
            ),
        imageTransformer = Coil3ImageTransformerImpl,
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun TuripMarkdownPreview() {
    TuripTheme {
        TuripMarkdown(
            content = "전주 한옥마을은 **조선시대**부터 이어진 전통 가옥이 밀집한 곳입니다.",
        )
    }
}
