package com.on.turip.feature.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation3.runtime.NavKey
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.main.navigation.model.NavigationIconModel
import com.on.turip.feature.main.navigation.model.NavigationItem
import com.on.turip.feature.mypage.api.MyPageNavKey
import com.on.turip.feature.turip.api.MyTuripNavKey

object TopLevel {
    val routes: Map<NavKey, NavigationItem> =
        mapOf(
            HomeNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Home),
                    label = "홈",
                ),
            MyTuripNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Folder),
                    label = "튜립",
                ),
            MyPageNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Person),
                    label = "마이",
                ),
        )
}
