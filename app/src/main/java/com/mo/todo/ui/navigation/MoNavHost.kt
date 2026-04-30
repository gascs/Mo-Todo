package com.mo.todo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mo.todo.ui.screen.memo.AddEditMemoScreen
import com.mo.todo.ui.screen.memo.MemoScreen
import com.mo.todo.ui.screen.profile.AboutScreen
import com.mo.todo.ui.screen.profile.DataManagementScreen
import com.mo.todo.ui.screen.profile.LabelManagementScreen
import com.mo.todo.ui.screen.profile.LegalScreen
import com.mo.todo.ui.screen.profile.PersonalizationScreen
import com.mo.todo.ui.screen.profile.ProfileScreen
import com.mo.todo.ui.screen.profile.ReminderSettingsScreen
import com.mo.todo.ui.screen.profile.WebDavConfigScreen
import com.mo.todo.ui.screen.todo.AddEditTodoScreen
import com.mo.todo.ui.screen.todo.TodoScreen

object MoRoutes {
    const val TODO = "todo"
    const val ADD_EDIT_TODO = "add_edit_todo?todoId={todoId}"
    const val MEMO = "memo"
    const val ADD_EDIT_MEMO = "add_edit_memo?memoId={memoId}"
    const val PROFILE = "profile"
    const val LABEL_MANAGEMENT = "label_management"
    const val REMINDER_SETTINGS = "reminder_settings"
    const val ABOUT = "about"
    const val PERSONALIZATION = "personalization"
    const val DATA_MANAGEMENT = "data_management"
    const val WEBDAC_CONFIG = "webdav_config"
    const val LEGAL = "legal/{type}"

    fun addEditTodoRoute(todoId: Long? = null) = if (todoId != null) "add_edit_todo?todoId=$todoId" else "add_edit_todo"
    fun addEditMemoRoute(memoId: Long? = null) = if (memoId != null) "add_edit_memo?memoId=$memoId" else "add_edit_memo"
}

@Composable
fun MoNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = MoRoutes.TODO, modifier = modifier) {
        composable(MoRoutes.TODO) { TodoScreen(onNavigateToAddEdit = { navController.navigate(MoRoutes.addEditTodoRoute(it)) }) }
        composable(MoRoutes.ADD_EDIT_TODO, arguments = listOf(navArgument("todoId") { type = NavType.LongType; defaultValue = -1L })) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("todoId") ?: -1L
            AddEditTodoScreen(todoId = if (id == -1L) null else id, onNavigateBack = { navController.popBackStack() })
        }
        composable(MoRoutes.MEMO) { MemoScreen(onNavigateToAddEdit = { navController.navigate(MoRoutes.addEditMemoRoute(it)) }) }
        composable(MoRoutes.ADD_EDIT_MEMO, arguments = listOf(navArgument("memoId") { type = NavType.LongType; defaultValue = -1L })) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("memoId") ?: -1L
            AddEditMemoScreen(memoId = if (id == -1L) null else id, onNavigateBack = { navController.popBackStack() })
        }
        composable(MoRoutes.PROFILE) {
            ProfileScreen(
                onNavigateToLabelManagement = { navController.navigate(MoRoutes.LABEL_MANAGEMENT) },
                onNavigateToReminderSettings = { navController.navigate(MoRoutes.REMINDER_SETTINGS) },
                onNavigateToAbout = { navController.navigate(MoRoutes.ABOUT) },
                onNavigateToPersonalization = { navController.navigate(MoRoutes.PERSONALIZATION) },
                onNavigateToDataManagement = { navController.navigate(MoRoutes.DATA_MANAGEMENT) }
            )
        }
        composable(MoRoutes.LABEL_MANAGEMENT) { LabelManagementScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(MoRoutes.REMINDER_SETTINGS) { ReminderSettingsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(MoRoutes.ABOUT) { AboutScreen(onNavigateBack = { navController.popBackStack() }, onNavigateToLegal = { type -> navController.navigate("legal/$type") }) }
        composable(MoRoutes.PERSONALIZATION) { PersonalizationScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(MoRoutes.DATA_MANAGEMENT) { DataManagementScreen(onNavigateBack = { navController.popBackStack() }, onNavigateToWebDavConfig = { navController.navigate(MoRoutes.WEBDAC_CONFIG) }) }
        composable(MoRoutes.WEBDAC_CONFIG) { WebDavConfigScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(MoRoutes.LEGAL, arguments = listOf(navArgument("type") { type = NavType.StringType })) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "privacy"
            LegalScreen(type = type, onNavigateBack = { navController.popBackStack() })
        }
    }
}
