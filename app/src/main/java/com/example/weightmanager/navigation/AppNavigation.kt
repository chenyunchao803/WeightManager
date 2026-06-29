package com.example.weightmanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.weightmanager.ui.screens.AddEditWeightScreen
import com.example.weightmanager.ui.screens.HomeScreen
import com.example.weightmanager.ui.screens.NutritionSearchScreen
import com.example.weightmanager.ui.screens.SettingsScreen
import com.example.weightmanager.ui.screens.StatsScreen
import com.example.weightmanager.viewmodel.NutritionViewModel
import com.example.weightmanager.viewmodel.SettingsViewModel
import com.example.weightmanager.viewmodel.WeightViewModel

object Routes {
    const val HOME = "home"
    const val ADD_EDIT_WEIGHT = "add_edit_weight?recordId={recordId}"
    const val STATS = "stats"
    const val NUTRITION_SEARCH = "nutrition_search"
    const val SETTINGS = "settings"

    fun addEditWeight(recordId: Long? = null): String {
        return if (recordId != null) {
            "add_edit_weight?recordId=$recordId"
        } else {
            "add_edit_weight"
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    weightViewModel: WeightViewModel,
    nutritionViewModel: NutritionViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                weightViewModel = weightViewModel
            )
        }

        composable(
            route = "add_edit_weight?recordId={recordId}",
            arguments = listOf(
                navArgument("recordId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: -1L
            AddEditWeightScreen(
                navController = navController,
                weightViewModel = weightViewModel,
                recordId = if (recordId > 0) recordId else null
            )
        }

        composable(Routes.STATS) {
            StatsScreen(
                navController = navController,
                weightViewModel = weightViewModel
            )
        }

        composable(Routes.NUTRITION_SEARCH) {
            NutritionSearchScreen(
                navController = navController,
                nutritionViewModel = nutritionViewModel
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
