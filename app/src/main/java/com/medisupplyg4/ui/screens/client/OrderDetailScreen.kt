package com.medisupplyg4.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medisupplyg4.R
import com.medisupplyg4.models.OrderItemUI
import com.medisupplyg4.ui.components.OrderStatusChip
import com.medisupplyg4.viewmodels.ClientOrdersViewModel
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: ClientOrdersViewModel = viewModel()
) {
    val orders by viewModel.orders.observeAsState(emptyList())
    val order = orders.find { it.id == orderId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.orders_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.visit_record_back)) }
                }
            )
        }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.orders_order_not_found))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OrderStatusChip(status = order.status)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(order.number, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        val fmt = DateTimeFormatter.ISO_LOCAL_DATE
                        Text("${stringResource(R.string.orders_created_at)} ${order.createdAt.format(fmt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        order.estimatedDelivery?.let { Text("${stringResource(R.string.orders_estimated_delivery)} ${it.format(fmt)}", style = MaterialTheme.typography.bodySmall) }
                        order.deliveredAt?.let { Text("${stringResource(R.string.orders_delivered_at)} ${it.format(fmt)}", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }

            item { Divider() }

            item {
                Text(stringResource(R.string.orders_products), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            items(order.items) { item ->
                OrderItemRow(item = item)
                Divider()
            }

            item {
                val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.orders_total_label), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(currency.format(order.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItemUI) {
    val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.orders_quantity, item.quantity), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(currency.format(item.unitPrice), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.orders_subtotal, currency.format(item.subtotal)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
