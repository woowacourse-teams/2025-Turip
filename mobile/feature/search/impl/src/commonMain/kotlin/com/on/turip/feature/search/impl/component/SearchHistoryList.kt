package com.on.turip.feature.search.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.SearchHistory
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SearchHistoryList(
    histories: ImmutableList<SearchHistory>,
    onHistoryClick: (keyword: String) -> Unit,
    onDeleteClick: (keyword: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TuripTheme.colors.white)
            .padding(horizontal = TuripTheme.spacing.extraLarge),
    ) {
        items(items = histories, key = { it.keyword }) { history ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable { onHistoryClick(history.keyword) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = history.keyword,
                    style = TuripTheme.typography.body2,
                    color = TuripTheme.colors.black,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = { onDeleteClick(history.keyword) },
                    modifier = Modifier.size(TuripTheme.spacing.extraExtraLarge),
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = TuripTheme.colors.gray02,
                    )
                }
            }
            HorizontalDivider(color = TuripTheme.colors.gray01, thickness = 1.dp)
        }
    }
}
