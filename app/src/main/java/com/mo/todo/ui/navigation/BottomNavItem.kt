package com.mo.todo.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.mo.todo.R
import compose.icons.Octicons
import compose.icons.octicons.CheckCircle24
import compose.icons.octicons.Note24
import compose.icons.octicons.Person24

sealed class BottomNavItem(val route: String, @StringRes val labelResId: Int, val icon: ImageVector) {
    data object Todo : BottomNavItem(route = "todo", labelResId = R.string.nav_todo, icon = Octicons.CheckCircle24)
    data object Memo : BottomNavItem(route = "memo", labelResId = R.string.nav_memo, icon = Octicons.Note24)
    data object Profile : BottomNavItem(route = "profile", labelResId = R.string.nav_profile, icon = Octicons.Person24)

    companion object {
        val items = listOf(Todo, Memo, Profile)
    }
}
