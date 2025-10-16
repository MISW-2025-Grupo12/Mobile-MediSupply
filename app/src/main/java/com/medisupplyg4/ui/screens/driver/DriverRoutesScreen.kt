package com.medisupplyg4.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.ui.components.SimplePeriodTabs
import com.medisupplyg4.ui.components.DeliveryGroupedByDay
import com.medisupplyg4.viewmodels.DeliveryRouteViewModel
import java.time.LocalDate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.medisupplyg4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRoutesScreen(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tabs de período
        SimplePeriodTabs(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { period ->
                viewModel.setSelectedPeriod(period)
            }
        )


        // Contenido principal
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            deliveries.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.inventory),
                            contentDescription = stringResource(R.string.no_deliveries),
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.no_deliveries_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                when (selectedPeriod) {
                    RoutePeriod.DAY -> {
                        // Para el día, mostrar entregas agrupadas por día
                        val groupedDeliveries = deliveries.groupBy { delivery ->
                            delivery.fechaEntrega.toLocalDate()
                        }.toSortedMap()

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            state = groupedListState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            groupedDeliveries.forEach { (_, dayDeliveries) ->
                                item {
                                    DeliveryGroupedByDay(
                                        deliveries = dayDeliveries
                                    )
                                }
                            }
                        }
                    }
                    RoutePeriod.WEEK, RoutePeriod.MONTH -> {
                        // Para semana y mes, mostrar entregas agrupadas por día
                        val groupedDeliveries = deliveries.groupBy { delivery ->
                            delivery.fechaEntrega.toLocalDate()
                        }.toSortedMap()

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            state = groupedListState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            groupedDeliveries.forEach { (_, dayDeliveries) ->
                                item {
                                    DeliveryGroupedByDay(
                                        deliveries = dayDeliveries
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
