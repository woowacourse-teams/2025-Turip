package com.on.turip.ui.compose.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation3.runtime.NavKey
import com.on.turip.R
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
                    labelRes = R.string.bottom_navigation_home,
                ),
            MyTuripNavKey to
                NavigationItem(
                    icon = Icons.Default.Email,
                    labelRes = R.string.bottom_navigation_my_turip,
                ),
            MyPageNavKey to
                NavigationItem(
                    icon = Icons.Default.Person,
                    labelRes = R.string.bottom_navigation_my_page,
                ),
        )
}
