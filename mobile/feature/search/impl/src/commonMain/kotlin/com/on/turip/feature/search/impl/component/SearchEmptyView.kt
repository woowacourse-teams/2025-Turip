package com.on.turip.feature.search.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun SearchEmptyView(
    keyword: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "'$keyword'\n검색 결과가 없어요",
            style = TuripTheme.typography.display,
            color = TuripTheme.colors.black,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = TuripTheme.colors.primary,
        )

        Text(
            text = "지역명으로 검색해보세요",
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray02,
        )
    }
}
