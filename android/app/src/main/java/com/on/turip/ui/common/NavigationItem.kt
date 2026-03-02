package com.on.turip.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.on.turip.ui.compose.designsystem.source.NoRippleInteractionSource
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import kotlinx.serialization.Serializable

@Immutable
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
)

/**
 * 다이얼로그 디자인 시스템의 하단 내비게이션 바.
 * 화면 하단에 메뉴 목록을 표시하고, 선택된 메뉴를 강조합니다.
 *
 * @param items 표시할 [NavigationItem] 리스트. 각 아이템은 라벨, 기본 아이콘, 선택 아이콘으로 구성됩니다.
 * @param selectedIndex 현재 선택된 아이템의 인덱스.
 * @param onSelectedIndexChange 아이템을 클릭했을 때 호출될 콜백. 클릭된 아이템의 인덱스를 전달합니다.
 * @param modifier 내비게이션 바에 적용할 [Modifier].
 */
@Composable
fun TuripNavigationBar(
    items: Map<NavKey, NavigationItem>,
    selectedKey: NavKey,
    onSelectedKeyChange: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(shadowElevation = 8.dp) {
        NavigationBar(
            containerColor = TuripTheme.colors.white,
            contentColor = TuripTheme.colors.black,
            modifier = modifier,
        ) {
            items.forEach { (navKey, item) ->
                val isSelected = selectedKey == navKey
                val iconColor = if (isSelected) TuripTheme.colors.gray03 else TuripTheme.colors.gray02
                val textColor = if (isSelected) TuripTheme.colors.gray03 else TuripTheme.colors.gray02

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectedKeyChange(navKey) },
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconColor,
                                modifier = Modifier.size(32.dp),
                            )
                            Text(
                                text = item.label,
                                style = TuripTheme.typography.info1,
                                color = textColor,
                            )
                        }
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = TuripTheme.colors.gray03,
                            unselectedIconColor = TuripTheme.colors.gray02,
                        ),
                    interactionSource = NoRippleInteractionSource,
                )
            }
        }
    }
}

@Serializable
private data object Example1 : NavKey

@Serializable
private data object Example2 : NavKey

@Serializable
private data object Example3 : NavKey

@Preview(showBackground = true, backgroundColor = 0)
@Composable
private fun TuripNavigationBarPreview() {
    var selectedIndex by remember { mutableStateOf(Example1) }
    TuripTheme {
        TuripNavigationBar(
            items =
                mapOf(
                    Example1 to
                        NavigationItem(
                            icon = Icons.Default.Home,
                            label = "홈화면",
                        ),
                    Example2 to
                        NavigationItem(
                            icon = Icons.Default.Email,
                            label = "내 튜립",
                        ),
                    Example3 to
                        NavigationItem(
                            icon = Icons.Default.Person,
                            label = "마이페이지",
                        ),
                ),
            selectedKey = selectedIndex,
            onSelectedKeyChange = { },
        )
    }
}
