package com.on.turip.ui.compose.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation3.runtime.NavKey
import com.on.turip.ui.common.model.navigation.NavigationItem
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.mypage.navigation.MyPageNavKey
import com.on.turip.ui.compose.turip.navigation.MyTuripNavKey

// TODO Res 빼기
object TopLevel {
    val routes: Map<NavKey, NavigationItem> =
        mapOf(
            HomeNavKey to
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "홈화면",
                ),
            MyTuripNavKey to
                NavigationItem(
                    icon = Icons.Default.Email,
                    label = "내 튜립",
                ),
            MyPageNavKey to
                NavigationItem(
                    icon = Icons.Default.Person,
                    label = "마이페이지",
                ),
        )
}
