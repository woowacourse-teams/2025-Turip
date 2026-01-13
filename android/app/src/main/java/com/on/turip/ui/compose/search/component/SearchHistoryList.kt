package com.on.turip.ui.compose.search.component

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun SearchHistoryList(
    histories: List<SearchHistory>,
    onHistoryClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .padding(horizontal = 20.dp),
    ) {
        items(
            items = histories,
            key = { it.keyword },
        ) { history ->
            SearchHistoryItem(
                keyword = history.keyword,
                onItemClick = { onHistoryClick(history.keyword) },
                onDeleteClick = { onDeleteClick(history.keyword) },
            )
            HorizontalDivider(
                color = TuripTheme.colors.gray01,
                thickness = 1.dp,
            )
        }
    }
}

@Composable
private fun SearchHistoryItem(
    keyword: String,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = keyword,
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(24.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search_cancel),
                contentDescription = "삭제",
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchHistoryListPreview() {
    val mockHistories =
        listOf(
            SearchHistory("제주도 맛집", 0L),
            SearchHistory("서울 가볼만한 곳", 0L),
            SearchHistory("부산 여행 코스", 0L),
            SearchHistory("강원도 차박", 0L),
        )
    TuripTheme {
        Surface {
            SearchHistoryList(
                histories = mockHistories,
                onHistoryClick = {},
                onDeleteClick = {},
            )
        }
    }
}
