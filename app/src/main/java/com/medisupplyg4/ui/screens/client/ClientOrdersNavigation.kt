package com.medisupplyg4.ui.screens.client

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ClientOrdersNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "orders_list") {
        composable("orders_list") {
            ClientOrdersScreen(onOrderSelected = { id ->
                navController.navigate("order_detail/$id")
            })
        }
        composable("order_detail/{orderId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(orderId = id, onBack = { navController.popBackStack() })
        }
    }
}
