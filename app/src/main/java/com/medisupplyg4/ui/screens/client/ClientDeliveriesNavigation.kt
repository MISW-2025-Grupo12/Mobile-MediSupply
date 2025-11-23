package com.medisupplyg4.ui.screens.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.viewmodels.ClientDeliveriesViewModel

@Composable
fun ClientDeliveriesNavigation(
    navController: NavHostController = rememberNavController()
) {
    val sharedViewModel: ClientDeliveriesViewModel = viewModel()
    
    NavHost(navController = navController, startDestination = "deliveries_list") {
        composable("deliveries_list") {
            ClientDeliveriesScreen(
                viewModel = sharedViewModel,
                onDeliverySelected = { delivery ->
                    // Navegar al detalle usando el ID de la entrega
                    navController.navigate("delivery_detail/${delivery.id}")
                }
            )
        }
        composable("delivery_detail/{deliveryId}") { backStackEntry ->
            val deliveryId = backStackEntry.arguments?.getString("deliveryId") ?: ""
            // Obtener la entrega del ViewModel usando el ID
            val deliveries by sharedViewModel.deliveries.observeAsState(emptyList())
            val delivery = deliveries.find { delivery: SimpleDelivery -> delivery.id == deliveryId }
            
            if (delivery != null) {
                DeliveryDetailScreen(
                    delivery = delivery,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

