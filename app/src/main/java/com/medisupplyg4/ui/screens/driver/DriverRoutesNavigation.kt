package com.medisupplyg4.ui.screens.driver

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun DriverRoutesNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "routes_list"
    ) {
        composable("routes_list") {
            DriverRoutesScreen(
                navController = navController
            )
        }
        
        composable("route_detail/{rutaId}") { backStackEntry ->
            val rutaId = backStackEntry.arguments?.getString("rutaId") ?: ""
            RouteDetailScreen(
                rutaId = rutaId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

