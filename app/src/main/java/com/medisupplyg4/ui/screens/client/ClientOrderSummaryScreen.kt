package com.medisupplyg4.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.medisupplyg4.R
import com.medisupplyg4.models.ItemPedido
import com.medisupplyg4.viewmodels.PedidosViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Screen to display order summary and confirm the order from client perspective
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientOrderSummaryScreen(
    viewModel: PedidosViewModel,
    onBackClick: () -> Unit,
    onOrderCreated: () -> Unit,
) {
    val context = LocalContext.current

    // Observe ViewModel state
    val clienteSeleccionado by viewModel.clienteSeleccionado.observeAsState(null)
    val itemsPedido by viewModel.itemsPedido.observeAsState(emptyList())
    val totalPedido by viewModel.totalPedido.observeAsState(0.0)
    val isCreatingOrder by viewModel.isCreatingOrder.observeAsState(false)
    val success by viewModel.success.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    // Success dialog state
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Error dialog state
    var showErrorDialog by remember { mutableStateOf(false) }

    // Para cliente, el vendedorId puede ser vacío si el backend lo maneja automáticamente
    // Por ahora usaremos un string vacío, el backend debería asignar un vendedor
    val vendedorId = "" // El backend debería asignar un vendedor automáticamente

    // Observe success changes
    LaunchedEffect(success) {
        if (success) {
            showSuccessDialog = true
        }
    }

    // Observe error changes
    LaunchedEffect(error) {
        if (error != null) {
            showErrorDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.order_summary_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Total and confirm button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.total)} ${formatPrice(totalPedido)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = { viewModel.crearPedido(vendedorId) },
                    enabled = itemsPedido.isNotEmpty() && !isCreatingOrder
                ) {
                    if (isCreatingOrder) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.creating_order))
                    } else {
                        Text(stringResource(R.string.confirm_order))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Client info (read-only, showing logged-in client)
            clienteSeleccionado?.let { cliente ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.client_field),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = cliente.nombre,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order items
            if (itemsPedido.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_products_added_to_order),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(itemsPedido) { item ->
                        OrderItemCard(
                            item = item,
                            onQuantityChange = { newQuantity ->
                                viewModel.actualizarCantidadProducto(item.producto.id, newQuantity)
                            },
                            onRemove = {
                                viewModel.removerProducto(item.producto.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Dialog is not dismissible */ },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.order_created_success),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.order_created_message),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.limpiarPedido()
                        onOrderCreated()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.continue_button))
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog && error != null) {
        val localizedErrorMessage = when (error) {
            PedidosViewModel.ERROR_CREATING_ORDER -> stringResource(R.string.order_error)
            PedidosViewModel.ERROR_NETWORK_CONNECTION -> stringResource(R.string.order_network_error)
            PedidosViewModel.ERROR_CLIENT_REQUIRED -> stringResource(R.string.validation_client_required_order)
            PedidosViewModel.ERROR_NO_PRODUCTS_IN_ORDER -> stringResource(R.string.validation_no_products_in_order)
            PedidosViewModel.ERROR_INSUFFICIENT_INVENTORY -> stringResource(R.string.validation_insufficient_inventory)
            else -> error
        }

        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                viewModel.clearError()
            },
            title = { Text(stringResource(R.string.error)) },
            text = { Text(localizedErrorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { 
                    showErrorDialog = false
                    viewModel.clearError()
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun OrderItemCard(
    item: ItemPedido,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image with fallback
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!item.producto.avatar.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.producto.avatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        error = painterResource(R.drawable.ic_launcher_foreground),
                        placeholder = painterResource(R.drawable.ic_launcher_foreground)
                    )
                } else {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.producto.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatPrice(item.producto.precio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        if (item.cantidad > 1) {
                            onQuantityChange(item.cantidad - 1)
                        } else {
                            onRemove()
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = stringResource(R.string.remove),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = item.cantidad.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                IconButton(
                    onClick = { 
                        // TODO: Get inventory from ViewModel to check availability
                        onQuantityChange(item.cantidad + 1)
                    },
                    enabled = true // TODO: Check against actual inventory
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val currentLocale = Locale.getDefault()
    val formatter = if (currentLocale.language == "en") {
        // English format: $1,234.56
        NumberFormat.getCurrencyInstance(Locale.US)
    } else {
        // Spanish format: $1.234,56
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("es").setRegion("CO").build())
    }
    return formatter.format(price)
}

