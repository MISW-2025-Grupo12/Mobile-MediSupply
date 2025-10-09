package com.medisupplyg4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.medisupplyg4.ui.screens.WorkingRoutesScreen

@Composable
fun MediSupplyNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "routes",
        modifier = modifier
    ) {
            composable("routes") {
                WorkingRoutesScreen()
            }
    }
}
