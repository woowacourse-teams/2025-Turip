package com.on.turip.feature.turip.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.mascot
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.turip.TuripType
import com.on.turip.feature.turip.impl.component.MyTuripCard
import com.on.turip.feature.turip.impl.component.MyTuripTabRow
import com.on.turip.feature.turip.impl.model.MyTuripModel
import com.on.turip.feature.turip.impl.model.MyTuripTab
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyTuripScreen(
    onNavigateToTuripDetail: (turipId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyTuripScreenContent(
        turips = sampleTurips,
        isLoading = false,
        onTuripClick = onNavigateToTuripDetail,
        onTuripDelete = {},
        onAddClick = onNavigateToLogin,
        modifier = modifier,
    )
}

@Composable
private fun MyTuripScreenContent(
    turips: List<MyTuripModel>,
    isLoading: Boolean,
    onTuripClick: (turipId: Long) -> Unit,
    onTuripDelete: (myTuripModel: MyTuripModel) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab: MyTuripTab by rememberSaveable { mutableStateOf(MyTuripTab.ALL) }
    var isDeleteMode: Boolean by rememberSaveable { mutableStateOf(false) }

    val filteredTurips by remember(turips, selectedTab) {
        derivedStateOf {
            when (selectedTab) {
                MyTuripTab.ALL -> turips
                MyTuripTab.SOLO -> turips.filter { it.type == TuripType.SOLO }
                MyTuripTab.TOGETHER -> turips.filter { it.type == TuripType.TOGETHER }
            }
        }
    }
    val userTuripCount by remember(turips) {
        derivedStateOf { turips.count { !it.isDefault } }
    }

    LaunchedEffect(userTuripCount, isDeleteMode) {
        if (isDeleteMode && userTuripCount == 0) {
            isDeleteMode = false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(TuripTheme.colors.white)) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = TuripTheme.spacing.extraLarge)
                    .pointerInput(isDeleteMode) {
                        detectTapGestures {
                            if (isDeleteMode) isDeleteMode = false
                        }
                    },
        ) {
            Text(
                text = "내 튜립",
                style = TuripTheme.typography.display,
                color = TuripTheme.colors.black,
                modifier =
                    Modifier.padding(
                        top = TuripTheme.spacing.extraExtraLarge,
                        bottom = TuripTheme.spacing.large,
                    ),
            )

            MyTuripTabRow(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    isDeleteMode = false
                },
            )

            if (!isLoading) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = TuripTheme.spacing.large),
                ) {
                    items(items = filteredTurips, key = { it.id }) { turip ->
                        MyTuripCard(
                            turipImage = turip.image,
                            turip = turip,
                            isDeleteMode = isDeleteMode,
                            onTuripClick = { id ->
                                if (isDeleteMode) {
                                    onTuripDelete(turip)
                                } else {
                                    onTuripClick(id)
                                }
                            },
                            onLongPress = { isDeleteMode = true },
                            onDeleteClick = { onTuripDelete(turip) },
                            isDefaultFolder = turip.isDefault,
                        )
                    }
                }

                if (selectedTab == MyTuripTab.TOGETHER && filteredTurips.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

                        Image(
                            painter = painterResource(Res.drawable.mascot),
                            contentDescription = null,
                            modifier = Modifier.size(90.dp),
                        )

                        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

                        Text(
                            text = "친구와 함께 하는 튜립이 없어요",
                            style = TuripTheme.typography.title1,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

                        Text(
                            text = "나홀로 튜립을 링크로 초대해서\n친구와 함께 여행을 떠나보세요.",
                            style = TuripTheme.typography.title2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = TuripTheme.colors.primary,
            contentColor = TuripTheme.colors.white,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(TuripTheme.spacing.extraLarge),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
        }
    }
}

private val sampleTurips =
    listOf(
        MyTuripModel(
            id = 0L,
            name = "기본 튜립",
            type = TuripType.SOLO,
            memberCount = 1,
            placeCount = 2,
            isDefault = true,
        ),
        MyTuripModel(
            id = 1L,
            name = "수원 여행 계획 튜립",
            type = TuripType.TOGETHER,
            memberCount = 3,
            placeCount = 2,
            isDefault = false,
        ),
        MyTuripModel(
            id = 2L,
            name = "도쿄 맛집 투어",
            type = TuripType.SOLO,
            memberCount = 1,
            placeCount = 3,
            isDefault = false,
        ),
    )

@Preview(showBackground = true)
@Composable
private fun MyTuripScreenPreview() {
    TuripTheme {
        MyTuripScreenContent(
            turips = sampleTurips,
            isLoading = false,
            onTuripClick = {},
            onTuripDelete = {},
            onAddClick = {},
        )
    }
}
