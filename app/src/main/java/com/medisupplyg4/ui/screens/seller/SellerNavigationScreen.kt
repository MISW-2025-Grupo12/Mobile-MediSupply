package com.medisupplyg4.ui.screens.seller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun SellerNavigationScreen(
    navController: NavHostController = rememberNavController()
) {
    var refreshVisits by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = "visits_list"
    ) {
        composable("visits_list") {
            SellerRoutesScreen(
                navController = navController,
                refreshTrigger = refreshVisits,
                onRefreshComplete = { refreshVisits = false }
            )
        }
        
        composable("visit_record/{visitaId}/{clienteId}/{clienteNombre}") { backStackEntry ->
            val visitaId = backStackEntry.arguments?.getString("visitaId") ?: ""
            val clienteId = backStackEntry.arguments?.getString("clienteId") ?: ""
            val clienteNombre = URLDecoder.decode(
                backStackEntry.arguments?.getString("clienteNombre") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            
            VisitRecordScreen(
                navController = navController,
                visitaId = visitaId,
                clienteId = clienteId,
                clienteNombre = clienteNombre,
                onVisitRecorded = { refreshVisits = true }
            )
        }

        // Nueva ruta: pantalla completa para subir evidencia
        composable("upload_evidence") {
            UploadEvidenceScreen(navController = navController)
        }
        
        composable("orders_list") {
            OrdersListScreen(
                onCreateOrderClick = {
                    navController.navigate("new_order")
                }
            )
        }
        
        composable("new_order") {
            NewOrderScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onViewOrderSummaryClick = {
                    navController.navigate("order_summary")
                }
            )
        }
        
        composable("order_summary") {
            // TODO: Get vendedorId from user session
            val vendedorId = "test-vendedor-id" // This should come from the logged-in user
            
            OrderSummaryScreen(
                vendedorId = vendedorId,
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderCreated = {
                    navController.navigate("orders_list") {
                        popUpTo("orders_list") { inclusive = true }
                    }
                }
            )
        }
    }
}
