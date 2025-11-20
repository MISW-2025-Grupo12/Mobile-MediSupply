package com.medisupplyg4.ui.screens.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medisupplyg4.R
import com.medisupplyg4.models.OrderUI
import com.medisupplyg4.ui.components.DateRangePickerModal
import com.medisupplyg4.ui.components.OrderCard
import com.medisupplyg4.viewmodels.SellerOrdersViewModel
import java.time.LocalDate

/**
 * Screen to display the orders list with FAB to create new order
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    onCreateOrderClick: () -> Unit,
    onOrderSelected: (String) -> Unit = {},
    viewModel: SellerOrdersViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val orders by viewModel.filteredOrders.observeAsState(emptyList())
    val startDate by viewModel.selectedStartDate.observeAsState(null)
    val endDate by viewModel.selectedEndDate.observeAsState(null)

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.orders_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateOrderClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.new_order_title)
                )
            }
        },
        modifier = modifier
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
                    TextButton(onClick = { viewModel.clearDateFilter() }) {
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.orders_not_found))
                }
            } else {
                OrdersList(orders = orders, onOrderSelected = onOrderSelected)
            }
        }
    }
}

@Composable
private fun OrdersList(orders: List<OrderUI>, onOrderSelected: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(orders) { order ->
            OrderCard(order = order, onClick = { onOrderSelected(order.id) })
        }
    }
}
