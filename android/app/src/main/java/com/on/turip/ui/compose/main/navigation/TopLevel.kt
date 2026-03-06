package com.on.turip.ui.compose.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation3.runtime.NavKey
import com.on.turip.R
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.main.navigation.model.NavigationIconModel
import com.on.turip.ui.compose.main.navigation.model.NavigationItem
import com.on.turip.ui.compose.mypage.navigation.MyPageNavKey
import com.on.turip.ui.compose.turip.navigation.MyTuripNavKey

object TopLevel {
    val routes: Map<NavKey, NavigationItem> =
        mapOf(
            HomeNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Home),
                    labelRes = R.string.bottom_navigation_home,
                ),
            MyTuripNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Folder),
                    labelRes = R.string.bottom_navigation_my_turip,
                ),
            MyPageNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Person),
                    labelRes = R.string.bottom_navigation_my_page,
                ),
        )
}
