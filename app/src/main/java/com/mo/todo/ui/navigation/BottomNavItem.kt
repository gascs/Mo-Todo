﻿﻿﻿﻿﻿package com.mo.todo.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.Octicons
import compose.icons.octicons.*

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Todo : BottomNavItem(
        route = "todo",
        label = "待办",
        icon = Octicons.CheckCircle24
    )

    data object Memo : BottomNavItem(
        route = "memo",
        label = "备忘",
        icon = Octicons.Note24
    )

    data object Profile : BottomNavItem(
        route = "profile",
        label = "我的",
        icon = Octicons.Person24
    )

    companion object {
        val items = listOf(Todo, Memo, Profile)
    }
}
