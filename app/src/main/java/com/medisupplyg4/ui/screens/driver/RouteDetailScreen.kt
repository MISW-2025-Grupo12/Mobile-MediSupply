package com.medisupplyg4.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.medisupplyg4.R
import com.medisupplyg4.models.BodegaDetail
import com.medisupplyg4.models.EntregaDetail
import com.medisupplyg4.models.ProductoPedidoDetail
import com.medisupplyg4.models.RouteDetail
import com.medisupplyg4.utils.DateFormatter
import com.medisupplyg4.viewmodels.RouteDetailViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    rutaId: String,
    routeFromList: RouteDetail? = null,
    onBack: () -> Unit,
    viewModel: RouteDetailViewModel = viewModel()
) {
    val routeDetail by viewModel.routeDetail.observeAsState(null)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    LaunchedEffect(rutaId, routeFromList) {
        // Si tenemos la ruta de la lista, usarla directamente
        if (routeFromList != null) {
            viewModel.setRouteDetail(routeFromList)
        } else {
            // Si no la tenemos, intentar cargar por ID o fecha
            if (rutaId.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                viewModel.loadRouteByDate(rutaId)
            } else {
                viewModel.loadRouteDetail(rutaId)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.route_detail_title)) },
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
        // Usar routeFromList si está disponible, sino usar routeDetail del ViewModel
        val currentRoute = routeFromList ?: routeDetail
        
        when {
            isLoading && currentRoute == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null && currentRoute == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.route_error),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            currentRoute == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.route_not_found))
                }
            }
            else -> {
                RouteDetailContent(
                    routeDetail = currentRoute,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun RouteDetailContent(
    routeDetail: RouteDetail,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información de la ruta
        item {
            RouteInfoCard(routeDetail = routeDetail, currencyFormat = currencyFormat)
        }

        // Lista de entregas
        item {
            Text(
                text = stringResource(R.string.route_deliveries, routeDetail.entregas.size),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(routeDetail.entregas) { entrega ->
            DeliveryDetailCard(entrega = entrega, currencyFormat = currencyFormat)
        }
    }
}

@Composable
private fun RouteInfoCard(
    routeDetail: RouteDetail,
    currencyFormat: NumberFormat
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fecha de ruta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.route_date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = DateFormatter.formatLongDate(routeDetail.fechaRutaLocalDate, context),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Divider()

            // Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.route_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = when (routeDetail.estado.lowercase()) {
                        "pendiente" -> MaterialTheme.colorScheme.tertiaryContainer
                        "en_proceso" -> MaterialTheme.colorScheme.primaryContainer
                        "completada" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = getRouteStatusString(routeDetail.estado),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Divider()

            // Bodega
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.route_warehouse),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = routeDetail.bodega.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = routeDetail.bodega.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DeliveryDetailCard(
    entrega: EntregaDetail,
    currencyFormat: NumberFormat
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header de entrega
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.route_delivery),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = DateFormatter.formatShortDateTime(entrega.fechaEntregaLocalDateTime, context),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            // Información del cliente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar del cliente
                AsyncImage(
                    model = entrega.pedido.cliente.avatar,
                    contentDescription = stringResource(R.string.customer_avatar),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.person),
                    placeholder = painterResource(id = R.drawable.person)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = entrega.pedido.cliente.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = entrega.direccion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = entrega.pedido.cliente.telefono,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider()

            // Información del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.route_order_total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currencyFormat.format(entrega.pedido.total),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.route_order_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = when (entrega.pedido.estado.lowercase()) {
                        "confirmado" -> MaterialTheme.colorScheme.primaryContainer
                        "en_transito" -> MaterialTheme.colorScheme.tertiaryContainer
                        "entregado" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = getOrderStatusString(entrega.pedido.estado),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Divider()

            // Productos del pedido
            Text(
                text = stringResource(R.string.route_products),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            entrega.pedido.productos.forEach { producto ->
                ProductItemRow(producto = producto, currencyFormat = currencyFormat)
            }
        }
    }
}

@Composable
private fun ProductItemRow(
    producto: ProductoPedidoDetail,
    currencyFormat: NumberFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del producto si existe
            if (!producto.avatar.isNullOrEmpty()) {
                AsyncImage(
                    model = producto.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.inventory),
                    placeholder = painterResource(id = R.drawable.inventory)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.orders_quantity, producto.cantidad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = currencyFormat.format(producto.precioUnitario),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.orders_subtotal, currencyFormat.format(producto.subtotal)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Obtiene el string traducido para el estado de la ruta
 */
@Composable
private fun getRouteStatusString(estado: String): String {
    return when (estado.lowercase()) {
        "pendiente" -> stringResource(R.string.route_status_pendiente)
        "en_proceso" -> stringResource(R.string.route_status_en_proceso)
        "completada" -> stringResource(R.string.route_status_completada)
        else -> estado
    }
}

/**
 * Obtiene el string traducido para el estado del pedido
 */
@Composable
private fun getOrderStatusString(estado: String): String {
    return when (estado.lowercase()) {
        "confirmado" -> stringResource(R.string.order_status_confirmed)
        "en_transito" -> stringResource(R.string.order_status_in_transit)
        "entregado" -> stringResource(R.string.order_status_delivered)
        "borrador" -> stringResource(R.string.order_status_draft)
        else -> estado
    }
}

