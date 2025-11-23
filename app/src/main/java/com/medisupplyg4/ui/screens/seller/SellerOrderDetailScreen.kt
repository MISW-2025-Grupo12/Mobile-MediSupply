package com.medisupplyg4.ui.screens.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.medisupplyg4.viewmodels.SellerOrdersViewModel
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: SellerOrdersViewModel = viewModel()
) {
    // Observar estados del ViewModel
    val orderDetail by viewModel.orderDetail.observeAsState(null)
    val cliente by viewModel.cliente.observeAsState(null)
    val isLoadingDetail by viewModel.isLoadingDetail.observeAsState(false)
    val isLoadingCliente by viewModel.isLoadingCliente.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    // Cargar el detalle del pedido cuando se entra a la pantalla
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    val isLoading = isLoadingDetail || isLoadingCliente

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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (orderDetail == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = error ?: stringResource(R.string.orders_order_not_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            return@Scaffold
        }

        val order = orderDetail!!

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del cliente
            cliente?.let { client ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.client),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = client.nombre,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = client.direccion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item { Divider() }
            }

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

