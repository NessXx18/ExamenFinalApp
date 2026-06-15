package com.examen.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
            // TODO: Navigate to LoginScreen
        }
        
        composable(Screen.ChatList.route) {
            // TODO: Navigate to ChatListScreen
        }
        
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            // TODO: Navigate to ChatScreen with sessionId
        }
        
        composable(Screen.Profile.route) {
            // TODO: Navigate to ProfileScreen
        }
    }
}
