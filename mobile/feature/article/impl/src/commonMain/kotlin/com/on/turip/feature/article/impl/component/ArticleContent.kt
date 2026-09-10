package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.theme.TuripTheme

/**
 * 아티클 본문.
 *
 * [content] 는 서버가 내려주는 **마크다운 원문**이다. 이미지도 `![](url)` 형태로 본문 안에 들어 있다.
 *
 * TODO: 마크다운 렌더러로 교체할 지점. 현재는 원문을 그대로 그린다.
 *  교체 시 이 함수 본문만 바꾸면 되고 호출부(ArticleDetailScreen)는 손댈 필요 없다.
 *  본문 좌우 여백은 20dp로, 홈 카드(16dp)와 일부러 다르게 둔다.
 *  이미지는 이 여백 없이 full-bleed로 그려 대비를 만드는 것이 원래 디자인 의도였다.
 */
@Composable
fun ArticleContent(
    content: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = content,
        color = TuripTheme.colors.gray05,
        style = TuripTheme.typography.body1,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.extraLarge),
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
