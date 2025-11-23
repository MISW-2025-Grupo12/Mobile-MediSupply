package com.medisupplyg4.ui.screens.driver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.models.RouteDetail
import com.medisupplyg4.viewmodels.DeliveryRouteViewModel

@Composable
fun DriverRoutesNavigation(
    navController: NavHostController = rememberNavController()
) {
    // ViewModel compartido para todas las pantallas
    val sharedViewModel: DeliveryRouteViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = "routes_list"
    ) {
        composable("routes_list") {
            DriverRoutesScreen(
                viewModel = sharedViewModel,
                navController = navController
            )
        }
        
        composable("route_detail/{rutaId}") { backStackEntry ->
            val rutaId = backStackEntry.arguments?.getString("rutaId") ?: ""
            // Buscar la ruta en la lista del ViewModel compartido
            val routes by sharedViewModel.routes.observeAsState(emptyList())
            val route = routes.find { it.id == rutaId }
            
            RouteDetailScreen(
                rutaId = rutaId,
                routeFromList = route, // Pasar la ruta si la encontramos
                onBack = { navController.popBackStack() }
            )
        }
    }
}

