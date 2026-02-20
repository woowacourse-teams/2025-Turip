package com.on.turip.ui.compose.myturip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.folder.component.MyTuripCard
import com.on.turip.ui.compose.folder.component.MyTuripModel
import com.on.turip.ui.compose.folder.component.MyTuripTabRow
import com.on.turip.ui.compose.folder.component.TuripType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class MyTuripTab(
    val tabName: String,
) {
    ALL("전체"),
    SOLO("나홀로 튜립"),
    TOGETHER("함께 튜립"),
}

@Composable
private fun MyTuripScreenContent(
    turips: ImmutableList<MyTuripModel>,
    onTuripClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab: MyTuripTab by rememberSaveable { mutableStateOf(MyTuripTab.ALL) }

    val filteredTurips: List<MyTuripModel> =
        when (selectedTab) {
            MyTuripTab.ALL -> turips
            MyTuripTab.SOLO -> turips.filter { it.type == TuripType.SOLO }
            MyTuripTab.TOGETHER -> turips.filter { it.type == TuripType.TOGETHER }
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TuripTheme.colors.white,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = TuripTheme.colors.primary,
                contentColor = TuripTheme.colors.white,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = TuripTheme.spacing.extraLarge),
        ) {
            Text(
                text = "내 튜립",
                style = TuripTheme.typography.display,
                color = Color.Black,
                modifier =
                    Modifier.padding(
                        top = TuripTheme.spacing.extraExtraLarge,
                        bottom = TuripTheme.spacing.large,
                    ),
            )

            MyTuripTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = TuripTheme.spacing.large),
            ) {
                items(items = filteredTurips, key = { it.id }) { turip ->
                    MyTuripCard(
                        turip = turip,
                        onTuripClick = onTuripClick,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTuripScreenPreview() {
    val allTurips: ImmutableList<MyTuripModel> =
        persistentListOf(
            MyTuripModel(0L, "수원 여행 계획 튜립", TuripType.TOGETHER, memberCount = 3, placeCount = 2),
            MyTuripModel(1L, "수원 여행 계획 튜립", TuripType.SOLO, placeCount = 1),
            MyTuripModel(2L, "수원 여행 계획 튜립", TuripType.SOLO, placeCount = 3),
        )

    TuripTheme {
        MyTuripScreenContent(allTurips,{},{})
    }
}
