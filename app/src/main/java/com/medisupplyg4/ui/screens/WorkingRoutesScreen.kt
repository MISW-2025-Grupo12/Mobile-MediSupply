package com.medisupplyg4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.ui.components.SimplePeriodTabs
import com.medisupplyg4.ui.components.SimpleDeliveryCard
import com.medisupplyg4.ui.components.DeliveryGroupedByDay
import com.medisupplyg4.ui.components.ScrollAwareDateSelector
import com.medisupplyg4.viewmodels.DeliveryRouteViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.ui.res.painterResource
import com.medisupplyg4.R


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

    // Estado de scroll para el contenido agrupado
    val groupedListState = rememberLazyListState()
    
    // Cargar datos cuando cambie el período o fecha
    LaunchedEffect(selectedPeriod, selectedDate) {
        viewModel.loadRoutes()
    }

    val navigationItems = listOf(
        "Rutas" to R.drawable.map,
        "Entregas" to R.drawable.inventory,
        "Perfil" to R.drawable.person,
    )

    val selectedItem = "Rutas"

        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rutas",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                navigationItems.forEach { (label, iconResId) ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = iconResId), // Usa el painter
                                contentDescription = label
                            )
                        },
                        label = { Text(label) },
                        selected = (selectedItem == label),
                        onClick = {
                            // TODO: agregar lógica de navegación
                            // navController.navigate(label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
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

        // Selector de fecha dinámico con scroll
        ScrollAwareDateSelector(
            deliveries = deliveries,
            selectedPeriod = selectedPeriod,
            listState = groupedListState,
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Intenta más tarde o verifica tu conexión",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                // Mostrar entregas según el período seleccionado
                when (selectedPeriod) {
                    RoutePeriod.DAY -> {
                        // Para día, mostrar lista simple
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(deliveries) { delivery ->
                                SimpleDeliveryCard(delivery = delivery)
                            }
                        }
                    }
                    RoutePeriod.WEEK, RoutePeriod.MONTH -> {
                        // Para semana y mes, mostrar agrupado por día
                        DeliveryGroupedByDay(
                            deliveries = deliveries,
                            listState = groupedListState,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
        }
    }
}

