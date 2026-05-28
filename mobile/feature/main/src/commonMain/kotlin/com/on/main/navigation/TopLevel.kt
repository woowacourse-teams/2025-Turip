package com.on.turip.feature.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation3.runtime.NavKey
import com.on.turip.feature.main.navigation.model.NavigationIconModel
import com.on.turip.feature.main.navigation.model.NavigationItem
import kotlinx.serialization.Serializable

// TODO: Remove stubs below and import actual NavKeys from feature api modules
@Serializable private data object HomeNavKey : NavKey // TODO: import from :feature:home:api
@Serializable private data object MyTuripNavKey : NavKey // TODO: import from :feature:myturip:api
@Serializable private data object MyPageNavKey : NavKey // TODO: import from :feature:mypage:api

object TopLevel {
    val routes: Map<NavKey, NavigationItem> =
        mapOf(
            HomeNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Home),
                    labelRes = 0, // TODO: R.string.bottom_navigation_home
                ),
            MyTuripNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Folder),
                    labelRes = 0, // TODO: R.string.bottom_navigation_my_turip
                ),
            MyPageNavKey to
                NavigationItem(
                    icon = NavigationIconModel.Vector(Icons.Default.Person),
                    labelRes = 0, // TODO: R.string.bottom_navigation_my_page
                ),
        )
}
