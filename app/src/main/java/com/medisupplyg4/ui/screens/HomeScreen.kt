package com.medisupplyg4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.R
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.ui.screens.driver.DriverRoutesScreen
import com.medisupplyg4.ui.screens.driver.DriverDeliveriesScreen
import com.medisupplyg4.ui.screens.seller.SellerNavigationScreen
import com.medisupplyg4.ui.screens.seller.SellerOrdersNavigationScreen

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
            Triple(R.drawable.inventory, stringResource(R.string.orders_title), "orders"),
            Triple(R.drawable.group_people, stringResource(R.string.clients), "clients"),
            Triple(R.drawable.person, stringResource(R.string.profile), "profile")
        )
        UserRole.CLIENT -> listOf(
            Triple(R.drawable.inventory, stringResource(R.string.orders_title), "orders"),
            Triple(R.drawable.history, stringResource(R.string.history), "history"),
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
                tabs.forEachIndexed { index, (iconRes, label) ->
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
                        0 -> SellerNavigationScreen()
                        1 -> SellerOrdersNavigationScreen()
                        2 -> {
                            // Clientes - no implementado aún
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.section_not_implemented),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        3 -> ProfileScreen(navController = navController)
                    }
                }
                UserRole.CLIENT -> {
                    // Cliente no implementado aún - mostrar mensaje
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.role_not_implemented),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    // Para otros roles, mostrar mensaje de no implementado
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.section_not_implemented),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

