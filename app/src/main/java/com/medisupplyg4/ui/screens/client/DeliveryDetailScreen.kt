package com.medisupplyg4.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.R
import com.medisupplyg4.models.OrderStatus
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.ui.components.OrderStatusChip
import com.medisupplyg4.utils.DateFormatter
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailScreen(
    delivery: SimpleDelivery,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.delivery_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.visit_record_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información de la entrega
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delivery_information),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.delivery_address),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = delivery.direccion,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.delivery_date, DateFormatter.formatShortDateTime(delivery.fechaEntrega, context)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Información del pedido si está disponible
            if (delivery.pedidoId != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                delivery.pedidoEstado?.let { estadoString ->
                                    val estado = remember(estadoString) {
                                        mapBackendStatusToOrderStatus(estadoString)
                                    }
                                    if (estado != null) {
                                        OrderStatusChip(status = estado)
                                    } else {
                                        Text(
                                            text = estadoString,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.order_information),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.order_id, delivery.pedidoId),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider()
                            delivery.pedidoFechaConfirmacion?.let { fechaConfirmacion ->
                                val fecha = remember(fechaConfirmacion) {
                                    try {
                                        LocalDateTime.parse(fechaConfirmacion)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                fecha?.let {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.order_confirmation_date),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = DateFormatter.formatShortDateTime(it, context),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(start = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Productos
            item {
                Text(
                    text = stringResource(R.string.orders_products),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(delivery.productos) { item ->
                DeliveryProductRow(item = item)
                HorizontalDivider()
            }

            // Total del pedido si está disponible
            if (delivery.pedidoTotal != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.orders_total_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            currency.format(delivery.pedidoTotal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mapea el estado del backend (en español) al enum OrderStatus
 */
private fun mapBackendStatusToOrderStatus(backendStatus: String): OrderStatus? {
    return when (backendStatus.lowercase()) {
        "borrador" -> OrderStatus.BORRADOR
        "confirmado" -> OrderStatus.CONFIRMADO
        "en tránsito", "en_transito", "en transito" -> OrderStatus.EN_TRANSITO
        "entregado" -> OrderStatus.ENTREGADO
        else -> null
    }
}

@Composable
private fun DeliveryProductRow(item: com.medisupplyg4.models.ItemPedido) {
    val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.producto.nombre,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                stringResource(R.string.orders_quantity, item.cantidad),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                currency.format(item.producto.precio),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                stringResource(R.string.orders_subtotal, currency.format(item.subtotal)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

