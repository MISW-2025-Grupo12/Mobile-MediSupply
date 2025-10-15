package com.medisupplyg4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.R
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.ui.screens.driver.DriverRoutesScreen
import com.medisupplyg4.ui.screens.driver.DriverDeliveriesScreen
import com.medisupplyg4.ui.screens.seller.SellerRoutesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userRole: UserRole = UserRole.DRIVER,
    navController: NavController = rememberNavController()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // Definir tabs según el rol del usuario
    val tabs = when (userRole) {
        UserRole.DRIVER -> listOf(
            Triple(R.drawable.map, stringResource(R.string.routes), "routes"),
            Triple(R.drawable.inventory, stringResource(R.string.deliveries), "deliveries"),
            Triple(R.drawable.person, stringResource(R.string.profile), "profile")
        )
        UserRole.SELLER -> listOf(
            Triple(R.drawable.map, stringResource(R.string.visit_routes), "routes"),
            Triple(R.drawable.group_people, stringResource(R.string.clients), "clients"),
            Triple(R.drawable.person, stringResource(R.string.profile), "profile")
        )
        else -> {
            // Para otros roles, mostrar mensaje de no implementado
            emptyList()
        }
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, (iconRes, label, route) ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(iconRes), contentDescription = label) },
                        label = { Text(label) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            // Aquí se manejará la navegación a cada sección
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Contenido principal según la tab seleccionada
            when (userRole) {
                UserRole.DRIVER -> {
                    when (selectedTabIndex) {
                        0 -> DriverRoutesScreen(navController = navController)
                        1 -> DriverDeliveriesScreen(navController = navController)
                        2 -> ProfileScreen(navController = navController)
                    }
                }
                UserRole.SELLER -> {
                    when (selectedTabIndex) {
                        0 -> SellerRoutesScreen(navController = navController)
                        1 -> {
                            // Clientes - no implementado aún
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sección no implementada aún",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        2 -> ProfileScreen(navController = navController)
                    }
                }
                else -> {
                    // Para otros roles, mostrar mensaje de no implementado
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Funcionalidad no implementada para este rol",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

