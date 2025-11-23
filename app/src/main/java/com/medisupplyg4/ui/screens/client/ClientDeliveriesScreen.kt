package com.medisupplyg4.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medisupplyg4.R
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.ui.components.DeliveryGroupedByDay
import com.medisupplyg4.ui.components.DateRangePickerModal
import com.medisupplyg4.utils.SessionManager
import com.medisupplyg4.viewmodels.ClientDeliveriesViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDeliveriesScreen(
    viewModel: ClientDeliveriesViewModel = viewModel(),
    onDeliverySelected: (SimpleDelivery) -> Unit = {}
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(false)
    val deliveries by viewModel.deliveries.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState(null)
    val startDate by viewModel.selectedStartDate.observeAsState(null)
    val endDate by viewModel.selectedEndDate.observeAsState(null)

    var showDatePicker by remember { mutableStateOf(false) }

    // Obtener el ID del cliente logueado
    val clienteId = SessionManager.getUserId(context) ?: ""

    // Cargar entregas cuando se monta el composable o cambian las fechas
    LaunchedEffect(clienteId, startDate, endDate) {
        if (clienteId.isNotEmpty()) {
            viewModel.loadDeliveries(clienteId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.deliveries)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Filtro por rango de fechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val startText = startDate?.toString() ?: "-"
                    val endText = endDate?.toString() ?: "-"
                    Text(
                        text = stringResource(R.string.orders_date_range, startText, endText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.orders_select))
                    }
                    TextButton(onClick = { 
                        viewModel.setDateRange(null, null)
                    }) {
                        Text(stringResource(R.string.orders_clear))
                    }
                }
            }

            if (showDatePicker) {
                DateRangePickerModal(
                    onDateRangeSelected = { (startMillis, endMillis) ->
                        val s = startMillis?.let { LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000)) }
                        val e = endMillis?.let { LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000)) }
                        viewModel.setDateRange(s, e)
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (deliveries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_deliveries_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        DeliveryGroupedByDay(
                            deliveries = deliveries,
                            onDeliveryClick = onDeliverySelected
                        )
                    }
                }
            }
        }
    }
}

