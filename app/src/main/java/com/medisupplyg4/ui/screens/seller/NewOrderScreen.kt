package com.medisupplyg4.ui.screens.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.MutableLiveData
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.medisupplyg4.R
import com.medisupplyg4.models.ProductoConInventario
import com.medisupplyg4.viewmodels.PedidosViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Screen to create a new order with client selection and product search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    viewModel: PedidosViewModel = viewModel(),
    onBackClick: () -> Unit,
    onViewOrderSummaryClick: () -> Unit
) {
    // Observe ViewModel state
    val clientesFiltrados by viewModel.clientesFiltrados.observeAsState(emptyList())
    val clientSearchQuery by viewModel.clientSearchQuery.observeAsState("")
    val productosFiltrados by viewModel.productosFiltrados.observeAsState(emptyList())
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val clienteSeleccionado by viewModel.clienteSeleccionado.observeAsState(null)
    val itemsPedido by viewModel.itemsPedido.observeAsState(emptyList())
    val isLoadingClientes by viewModel.isLoadingClientes.observeAsState(false)
    val isLoadingProductos by viewModel.isLoadingProductos.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    // Error dialog state
    var showErrorDialog by remember { mutableStateOf(false) }
    
    // Client validation state
    var showClientValidationError by remember { mutableStateOf(false) }
    
    // Auto-hide validation error after 3 seconds
    LaunchedEffect(showClientValidationError) {
        if (showClientValidationError) {
            kotlinx.coroutines.delay(3000)
            showClientValidationError = false
        }
    }

    // Load clients when entering the screen
    LaunchedEffect(Unit) {
        viewModel.loadClientes()
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
                title = { Text(stringResource(R.string.new_order_title)) },
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
            // Order summary button
            val totalItems = itemsPedido.sumOf { it.cantidad }
            Button(
                onClick = {
                val validationError = viewModel.validarPedido()
                if (validationError != null) {
                    // Check if it's a client validation error
                    if (validationError == PedidosViewModel.ERROR_CLIENT_REQUIRED) {
                        showClientValidationError = true
                    } else {
                        // Show other errors in dialog
                        viewModel.clearError()
                        (viewModel.error as MutableLiveData).value = validationError
                    }
                } else {
                    showClientValidationError = false
                    onViewOrderSummaryClick()
                }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = totalItems > 0
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.view_order_summary) + " ($totalItems)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Client Search with Material Design 3 SearchBar
            var clientSearchExpanded by remember { mutableStateOf(false) }
            
            SearchBar(
                query = if (clientSearchExpanded) clientSearchQuery else (clienteSeleccionado?.nombre ?: ""),
                onQueryChange = { 
                    viewModel.setClientSearchQuery(it)
                    // Clear validation error when user starts typing
                    if (showClientValidationError) {
                        showClientValidationError = false
                    }
                    // Only clear selected client if the search query is empty
                    if (it.isEmpty()) {
                        viewModel.setClienteSeleccionado(null)
                    }
                },
                onSearch = { clientSearchExpanded = false },
                active = clientSearchExpanded,
                onActiveChange = { 
                    clientSearchExpanded = it
                    // Clear validation error when user opens search
                    if (it && showClientValidationError) {
                        showClientValidationError = false
                    }
                    // When closing the search, clear the search query if no client is selected
                    if (!it && clienteSeleccionado == null) {
                        viewModel.setClientSearchQuery("")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (showClientValidationError) {
                            Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                placeholder = { Text(stringResource(R.string.search_clients)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = if (showClientValidationError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            ) {
                // Client search results
                if (isLoadingClientes) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (clientesFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_clients_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(clientesFiltrados) { cliente ->
                            ListItem(
                                headlineContent = { Text(cliente.nombre) },
                                supportingContent = { 
                                    Column {
                                        Text(cliente.email)
                                        Text(cliente.telefono)
                                    }
                                },
                                modifier = Modifier.clickable {
                                    viewModel.setClienteSeleccionado(cliente)
                                    clientSearchExpanded = false
                                    // Don't clear the search query, let it show the selected client name
                                }
                            )
                        }
                    }
                }
            }

            // Client validation error text
            if (showClientValidationError) {
                Text(
                    text = stringResource(R.string.validation_client_required_order),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text(stringResource(R.string.search_products)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Products List
            if (isLoadingProductos) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_products_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosFiltrados) { productoConInventario ->
                        ProductoCard(
                            productoConInventario = productoConInventario,
                            onAddClick = { viewModel.agregarProducto(productoConInventario) }
                        )
                    }
                }
            }
        }
    }


    // Error Dialog
    if (showErrorDialog && error != null) {
        val localizedErrorMessage = when (error) {
            PedidosViewModel.ERROR_CREATING_ORDER -> stringResource(R.string.order_error)
            PedidosViewModel.ERROR_NETWORK_CONNECTION -> stringResource(R.string.order_network_error)
            PedidosViewModel.ERROR_CLIENT_REQUIRED -> stringResource(R.string.validation_client_required_order)
            PedidosViewModel.ERROR_SEARCH_TOO_LONG -> stringResource(R.string.validation_search_too_long)
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
private fun ProductoCard(
    productoConInventario: ProductoConInventario,
    onAddClick: () -> Unit,
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
                if (!productoConInventario.avatar.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(productoConInventario.avatar)
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
                    text = productoConInventario.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatPrice(productoConInventario.precio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${productoConInventario.cantidadDisponible} ${stringResource(R.string.units_available)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Add button
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
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
