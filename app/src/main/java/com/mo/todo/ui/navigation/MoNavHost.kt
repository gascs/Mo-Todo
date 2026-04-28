package com.mo.todo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mo.todo.ui.screen.memo.AddEditMemoScreen
import com.mo.todo.ui.screen.memo.MemoScreen
import com.mo.todo.ui.screen.profile.ProfileScreen
import com.mo.todo.ui.screen.todo.AddEditTodoScreen
import com.mo.todo.ui.screen.todo.TodoScreen

object MoRoutes {
    const val TODO = "todo"
    const val ADD_EDIT_TODO = "add_edit_todo?todoId={todoId}"
    const val MEMO = "memo"
    const val ADD_EDIT_MEMO = "add_edit_memo?memoId={memoId}"
    const val PROFILE = "profile"

    fun addEditTodoRoute(todoId: Long? = null): String {
        return if (todoId != null) "add_edit_todo?todoId=$todoId" else "add_edit_todo"
    }

    fun addEditMemoRoute(memoId: Long? = null): String {
        return if (memoId != null) "add_edit_memo?memoId=$memoId" else "add_edit_memo"
    }
}

@Composable
fun MoNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MoRoutes.TODO
    ) {
        composable(MoRoutes.TODO) {
            TodoScreen(
                onNavigateToAddEdit = { todoId ->
                    navController.navigate(MoRoutes.addEditTodoRoute(todoId))
                }
            )
        }

        composable(
            route = MoRoutes.ADD_EDIT_TODO,
            arguments = listOf(
                navArgument("todoId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getLong("todoId") ?: -1L
            AddEditTodoScreen(
                todoId = if (todoId == -1L) null else todoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(MoRoutes.MEMO) {
            MemoScreen(
                onNavigateToAddEdit = { memoId ->
                    navController.navigate(MoRoutes.addEditMemoRoute(memoId))
                }
            )
        }

        composable(
            route = MoRoutes.ADD_EDIT_MEMO,
            arguments = listOf(
                navArgument("memoId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val memoId = backStackEntry.arguments?.getLong("memoId") ?: -1L
            AddEditMemoScreen(
                memoId = if (memoId == -1L) null else memoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(MoRoutes.PROFILE) {
            ProfileScreen()
        }
    }
}
