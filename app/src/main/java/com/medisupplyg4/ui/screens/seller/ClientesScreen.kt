package com.medisupplyg4.ui.screens.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.viewmodels.ClientesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel = viewModel(),
    navController: NavController = rememberNavController(),
    token: String = ""
) {
    val clientes by viewModel.filteredClientes.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val isEmpty by viewModel.isEmpty.observeAsState(false)
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val selectedStatus by viewModel.selectedStatus.observeAsState("TODOS")

    val context = LocalContext.current

    // Cargar clientes al iniciar
    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadClientes(token)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = stringResource(R.string.clients),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_clients_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Filtro por estado
        val estadosLocalizados = listOf(
            stringResource(R.string.all_status),
            stringResource(R.string.active_status),
            stringResource(R.string.inactive_status)
        )
        
        val allStatusString = stringResource(R.string.all_status)
        val activeStatusString = stringResource(R.string.active_status)
        val inactiveStatusString = stringResource(R.string.inactive_status)
        
        StatusDropdown(
            selectedStatus = getLocalizedStatusString(selectedStatus),
            onStatusSelected = { localizedStatus ->
                val internalStatus = when (localizedStatus) {
                    allStatusString -> "TODOS"
                    activeStatusString -> "ACTIVO"
                    inactiveStatusString -> "INACTIVO"
                    else -> "TODOS"
                }
                viewModel.updateStatusFilter(internalStatus)
            },
            estadosDisponibles = estadosLocalizados,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

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
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error ?: stringResource(R.string.unknown_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { 
                                viewModel.clearError()
                                if (token.isNotEmpty()) {
                                    viewModel.refreshClientes(token)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
            isEmpty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.no_clients_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                // Lista de clientes
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clientes) { cliente ->
                        ClienteCard(
                            cliente = cliente,
                            onClick = {
                                // TODO: Navegar a detalles del cliente
                                // navController.navigate("client_details/${cliente.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClienteCard(
    cliente: ClienteAPI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del cliente (placeholder por ahora)
            Card(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del cliente
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cliente.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${stringResource(R.string.nit)}: ${cliente.identificacion}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Indicador de estado
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = getEstadoColor(cliente.estado),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = getLocalizedStatusString(cliente.estado),
                        style = MaterialTheme.typography.bodySmall,
                        color = getEstadoColor(cliente.estado),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Flecha de navegación
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.view_details),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusDropdown(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    estadosDisponibles: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.status)}: $selectedStatus",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.expand),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            estadosDisponibles.forEach { estado ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = estado,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onStatusSelected(estado)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Función para obtener el color según el estado del cliente
 */
@Composable
private fun getEstadoColor(estado: String): Color {
    return when (estado.uppercase()) {
        "ACTIVO" -> Color(0xFF4CAF50) // Verde para activo
        "INACTIVO" -> Color(0xFFF44336) // Rojo para inactivo
        else -> MaterialTheme.colorScheme.onSurfaceVariant // Gris para estados desconocidos
    }
}

/**
 * Función para obtener el string localizado del estado del cliente
 */
@Composable
private fun getLocalizedStatusString(estado: String): String {
    return when (estado.uppercase()) {
        "ACTIVO" -> stringResource(R.string.active_status)
        "INACTIVO" -> stringResource(R.string.inactive_status)
        "TODOS" -> stringResource(R.string.all_status)
        else -> estado // Fallback al original si no se reconoce
    }
}
