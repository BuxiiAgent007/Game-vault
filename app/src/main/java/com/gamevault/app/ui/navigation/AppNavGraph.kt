package com.gamevault.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gamevault.app.ui.screens.CalendarScreen
import com.gamevault.app.ui.screens.CollectionScreen
import com.gamevault.app.ui.screens.GameDetailScreen
import com.gamevault.app.ui.screens.HomeScreen
import com.gamevault.app.ui.screens.LoginScreen
import com.gamevault.app.ui.screens.ProfileScreen
import com.gamevault.app.ui.screens.RegisterScreen
import com.gamevault.app.ui.screens.SearchScreen
import com.gamevault.app.ui.screens.SettingsScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val startDestination =
        if (FirebaseAuth.getInstance().currentUser != null) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.SEARCH) { SearchScreen(navController) }
        composable(Routes.COLLECTION) { CollectionScreen(navController) }
        composable(Routes.CALENDAR) { CalendarScreen(navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("rawgId") { type = NavType.IntType })
        ) { GameDetailScreen(navController) }
    }
}
