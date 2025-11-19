package com.medisupplyg4.ui.screens.seller

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.utils.SessionManager
import com.medisupplyg4.viewmodels.PedidosViewModel

/**
 * Navigation screen specifically for seller orders functionality
 */
@Composable
fun SellerOrdersNavigationScreen(
    navController: NavHostController = rememberNavController()
) {
    // Create a shared ViewModel instance for all screens
    val sharedViewModel: PedidosViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = "orders_list"
    ) {
        composable("orders_list") {
            OrdersListScreen(
                onCreateOrderClick = {
                    navController.navigate("new_order")
                }
            )
        }
        
        composable("new_order") {
            NewOrderScreen(
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
            val context = LocalContext.current
            // Get entidad_id from user session (this is the vendedor's entidad_id)
            val vendedorId = SessionManager.getUserId(context) ?: ""
            
            OrderSummaryScreen(
                viewModel = sharedViewModel,
                vendedorId = vendedorId,
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
