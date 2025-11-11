package com.medisupplyg4.ui.screens.seller

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.medisupplyg4.models.VisitSuggestionsResponse
import kotlinx.coroutines.delay
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
        
        // Ruta para mostrar sugerencias de visita
        composable("visit_suggestions/{visitaId}") { backStackEntry ->
            val visitaId = backStackEntry.arguments?.getString("visitaId") ?: ""
            // Get suggestions from previous back stack entry (visit_record)
            val previousEntry = navController.previousBackStackEntry
            val suggestionsJsonFlow = previousEntry?.savedStateHandle?.getStateFlow<String?>("suggestions_json", null)
            val suggestionsJson by (suggestionsJsonFlow?.collectAsState(initial = null) ?: remember { mutableStateOf<String?>(null) })
            
            var suggestions by remember { mutableStateOf<VisitSuggestionsResponse?>(null) }
            var hasReadSuggestions by remember { mutableStateOf(false) }
            
            // Watch for suggestions in savedStateHandle
            LaunchedEffect(suggestionsJson) {
                if (!hasReadSuggestions && suggestionsJson != null) {
                    try {
                        val gson = Gson()
                        suggestions = gson.fromJson(suggestionsJson, VisitSuggestionsResponse::class.java)
                        // Clear the saved state after reading
                        previousEntry?.savedStateHandle?.remove<String>("suggestions_json")
                        hasReadSuggestions = true
                        // Don't pop back automatically - let the user see the screen
                        // The back button handlers will take care of navigation
                    } catch (e: Exception) {
                        // Handle error - navigate back to visits list if parsing fails
                        navController.navigate("visits_list") {
                            popUpTo("visits_list") { inclusive = true }
                        }
                    }
                }
            }
            
            // Handle back button press to go to visits list
            // First pop to remove visit_record from back stack, then navigate
            BackHandler(enabled = true) {
                // Pop back to visits_list, removing visit_record from back stack
                navController.popBackStack("visits_list", inclusive = false)
            }
            
            if (suggestions != null) {
                VisitSuggestionsScreen(
                    navController = navController,
                    suggestions = suggestions!!
                )
            } else {
                // Show loading while waiting for suggestions
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
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
