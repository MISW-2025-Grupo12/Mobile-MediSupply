package com.medisupplyg4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.ui.components.SimplePeriodTabs
import com.medisupplyg4.ui.components.SimpleDeliveryCard
import com.medisupplyg4.viewmodels.DeliveryRouteViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkingRoutesScreen(
    viewModel: DeliveryRouteViewModel = viewModel(),
    navController: NavController = rememberNavController()
) {
    // Usar el ViewModel real
    val deliveries by viewModel.deliveries.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val selectedPeriod by viewModel.selectedPeriod.observeAsState(RoutePeriod.DAY)
    val selectedDate by viewModel.selectedDate.observeAsState(LocalDate.now())

    // Cargar datos cuando cambie el período o fecha
    LaunchedEffect(selectedPeriod, selectedDate) {
        viewModel.loadRoutes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rutas",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Rutas") },
                    label = { Text("Rutas") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Entregas") },
                    label = { Text("Entregas") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
        // Tabs para seleccionar período
        SimplePeriodTabs(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { period ->
                viewModel.setSelectedPeriod(period)
            },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de fecha
        WorkingDateSelector(
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Contenido principal
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            deliveries.isEmpty() -> {
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
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No hay rutas disponibles",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Intenta más tarde o verifica tu conexión",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mostrar todas las entregas
                    items(deliveries) { delivery ->
                        SimpleDeliveryCard(delivery = delivery)
                    }
                }
            }
        }
        }
    }
}

@Composable
fun WorkingDateSelector(
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hoy",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = formatter.format(today),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendario",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Función para crear datos mock
private fun createMockRoutes(): List<com.medisupplyg4.models.DeliveryRoute> {
    val customers = listOf(
        com.medisupplyg4.models.Customer(
            id = "1",
            name = "Hospital San Rafael",
            address = "Calle 123 #45-67, Bogotá",
            phone = "+57 1 234-5678",
            email = "contacto@hospitalsanrafael.com",
            type = com.medisupplyg4.models.CustomerType.HOSPITAL
        ),
        com.medisupplyg4.models.Customer(
            id = "2", 
            name = "Clínica Santa María",
            address = "Avenida 7 #32-10, Bogotá",
            phone = "+57 1 345-6789",
            email = "info@clinicasantamaria.com",
            type = com.medisupplyg4.models.CustomerType.CLINIC
        ),
        com.medisupplyg4.models.Customer(
            id = "3",
            name = "Centro Médico Los Andes",
            address = "Carrera 15 #93-47, Bogotá", 
            phone = "+57 1 456-7890",
            email = "atencion@centromedicoandes.com",
            type = com.medisupplyg4.models.CustomerType.MEDICAL_CENTER
        )
    )
    
    val deliveryPoints = listOf(
        com.medisupplyg4.models.DeliveryPoint(
            id = "1",
            customer = customers[0],
            orderId = "34562",
            products = listOf(
                com.medisupplyg4.models.ProductDelivery("P1", "Jeringas 10ml", 50, "unidades"),
                com.medisupplyg4.models.ProductDelivery("P2", "Guantes Nitrilo", 100, "unidades")
            ),
            estimatedDeliveryTime = java.time.LocalDateTime.now().plusHours(2),
            priority = com.medisupplyg4.models.DeliveryPriority.HIGH,
            requiresColdChain = false,
            order = 1
        ),
        com.medisupplyg4.models.DeliveryPoint(
            id = "2",
            customer = customers[1],
            orderId = "34563", 
            products = listOf(
                com.medisupplyg4.models.ProductDelivery("P3", "Vacunas COVID-19", 20, "dosis", true),
                com.medisupplyg4.models.ProductDelivery("P4", "Tubos de Sangre", 30, "unidades")
            ),
            estimatedDeliveryTime = java.time.LocalDateTime.now().plusHours(4),
            priority = com.medisupplyg4.models.DeliveryPriority.URGENT,
            requiresColdChain = true,
            specialInstructions = "Mantener cadena de frío",
            order = 2
        ),
        com.medisupplyg4.models.DeliveryPoint(
            id = "3",
            customer = customers[2],
            orderId = "34564",
            products = listOf(
                com.medisupplyg4.models.ProductDelivery("P5", "Mascarillas N95", 200, "unidades"),
                com.medisupplyg4.models.ProductDelivery("P6", "Alcohol Gel", 50, "litros")
            ),
            estimatedDeliveryTime = java.time.LocalDateTime.now().plusHours(6),
            priority = com.medisupplyg4.models.DeliveryPriority.NORMAL,
            requiresColdChain = false,
            order = 3
        )
    )
    
    return listOf(
        com.medisupplyg4.models.DeliveryRoute(
            id = "route-${LocalDate.now()}",
            driverId = "driver-001",
            date = LocalDate.now(),
            deliveryPoints = deliveryPoints,
            totalDistance = 45.5,
            estimatedTotalTime = 180,
            status = com.medisupplyg4.models.RouteStatus.PLANNED
        )
    )
}
