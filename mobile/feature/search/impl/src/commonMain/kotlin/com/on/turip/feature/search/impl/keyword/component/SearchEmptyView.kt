package com.on.turip.feature.search.impl.keyword.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun SearchEmptyView(
    searchKeyword: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.search_result_no_result, searchKeyword),
            style = TuripTheme.typography.display,
            color = TuripTheme.colors.black,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = TuripTheme.colors.primary,
            )

            Text(
                text = stringResource(Res.string.search_result_tip),
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.primary,
            )

            Spacer(modifier = Modifier.width(TuripTheme.spacing.small))

            Text(
                text = stringResource(Res.string.search_result_tip_region),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.gray02,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchEmptyViewPreview() {
    TuripTheme {
        Surface {
            SearchEmptyView(searchKeyword = "구대구대")
        }
    }
}
