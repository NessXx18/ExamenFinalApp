package org.inovaapp.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import org.inovaapp.project.presentation.chat.ChatScreen
import org.inovaapp.project.presentation.chatlist.ChatListScreen
import org.inovaapp.project.presentation.login.LoginScreen
import org.inovaapp.project.presentation.profile.ProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{sessionId}") {
        fun createRoute(sessionId: String) = "chat/$sessionId"
    }
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ChatList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ChatList.route) {
            ChatListScreen(
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.Chat.createRoute(sessionId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(
            Screen.Chat.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            ChatScreen(
                sessionId = backStackEntry.arguments?.getString("sessionId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}