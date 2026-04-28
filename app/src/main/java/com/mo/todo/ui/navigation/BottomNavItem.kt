package com.mo.todo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Todo : BottomNavItem(
        route = "todo",
        label = "待办",
        icon = Icons.Outlined.CheckCircle
    )

    data object Memo : BottomNavItem(
        route = "memo",
        label = "备忘",
        icon = Icons.Outlined.NoteAlt
    )

    data object Profile : BottomNavItem(
        route = "profile",
        label = "我的",
        icon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Todo, Memo, Profile)
    }
}
