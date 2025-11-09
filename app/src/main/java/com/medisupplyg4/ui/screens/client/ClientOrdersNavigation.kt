package com.medisupplyg4.ui.screens.client

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.viewmodels.PedidosViewModel

@Composable
fun ClientOrdersNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Create a shared ViewModel instance for all screens
    val sharedViewModel: PedidosViewModel = viewModel()
    
    NavHost(navController = navController, startDestination = "orders_list") {
        composable("orders_list") {
            ClientOrdersScreen(
                onOrderSelected = { id ->
                    navController.navigate("order_detail/$id")
                },
                onCreateOrderClick = {
                    navController.navigate("new_order")
                }
            )
        }
        composable("order_detail/{orderId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(orderId = id, onBack = { navController.popBackStack() })
        }
        composable("new_order") {
            ClientNewOrderScreen(
                viewModel = sharedViewModel,
                onBackClick = {
                    // Refresh inventory when going back
                    sharedViewModel.refrescarInventario()
                    navController.popBackStack()
                },
                onViewOrderSummaryClick = {
                    navController.navigate("order_summary")
                }
            )
        }
        composable("order_summary") {
            ClientOrderSummaryScreen(
                viewModel = sharedViewModel,
                onBackClick = {
                    // Refresh inventory when going back from summary
                    sharedViewModel.refrescarInventario()
                    navController.popBackStack()
                },
                onOrderCreated = {
                    // Refresh inventory after creating order
                    sharedViewModel.refrescarInventario()
                    navController.navigate("orders_list") {
                        popUpTo("orders_list") { inclusive = true }
                    }
                }
            )
        }
    }
}
