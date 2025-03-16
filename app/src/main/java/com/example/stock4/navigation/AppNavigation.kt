package com.example.stock4.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stock4.ui.screens.AIAnalysisScreen
import com.example.stock4.ui.screens.HomeScreen
import com.example.stock4.ui.screens.ProfileScreen
import com.example.stock4.ui.screens.SearchScreen
import com.example.stock4.ui.screens.SettingsScreen
import com.example.stock4.ui.screens.SplashScreen

// 定义应用中的所有导航路径
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Search : Screen("search")
    object AIAnalysis : Screen("ai_analysis/{stockCode}") {
        fun createRoute(stockCode: String) = "ai_analysis/$stockCode"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(
            route = Screen.AIAnalysis.route,
            arguments = listOf(navArgument("stockCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val stockCode = backStackEntry.arguments?.getString("stockCode") ?: ""
            AIAnalysisScreen(navController = navController, stockCode = stockCode)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
} 