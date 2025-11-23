package com.medisupplyg4.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import com.medisupplyg4.models.RouteDetail
import com.medisupplyg4.R
import com.medisupplyg4.utils.DateFormatter
import java.time.LocalDate

@Composable
fun RoutesGroupedByDay(
    routes: List<RouteDetail>,
    onRouteClick: (RouteDetail) -> Unit = {},
    onDayClick: ((LocalDate) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Agrupar rutas por día
    val groupedRoutes = routes
        .sortedBy { it.fechaRutaLocalDate } // Ordenar por fecha de ruta (más antiguas primero)
        .groupBy { it.fechaRutaLocalDate }
        .toSortedMap(compareBy { it }) // Ordenar los días (más antiguos primero)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        groupedRoutes.forEach { (date, dayRoutes) ->
            // Header del día
            RouteDayHeader(
                date = date,
                onClick = onDayClick?.let { { it(date) } }
            )
            
            // Espaciado adicional
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rutas del día (ordenadas por fecha ascendente - más antiguas primero)
            dayRoutes.sortedBy { it.fechaRutaLocalDate }.forEach { route ->
                RouteCard(
                    route = route,
                    onClick = { onRouteClick(route) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RouteDayHeader(
    date: LocalDate,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = LocalDate.now()
    val isToday = date == today
    
    val cardModifier = if (onClick != null) {
        modifier.fillMaxWidth().clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }
    
    Card(
        modifier = cardModifier
            .border(
                width = 1.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) MaterialTheme.colorScheme.primaryContainer
                           else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isToday) stringResource(R.string.today) else DateFormatter.formatLongDate(date, context),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                       else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RouteCard(
    route: RouteDetail,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Información de la bodega
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.bodega.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = route.bodega.direccion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Estado de la ruta
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (route.estado.lowercase()) {
                        "pendiente" -> MaterialTheme.colorScheme.tertiaryContainer
                        "en_proceso" -> MaterialTheme.colorScheme.primaryContainer
                        "completada" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = getRouteStatusString(route.estado),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = when (route.estado.lowercase()) {
                            "pendiente" -> MaterialTheme.colorScheme.onTertiaryContainer
                            "en_proceso" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "completada" -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Divider()
            
            // Información de entregas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.route_deliveries, route.entregas.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Flecha
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(R.string.delivery_view_details),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
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

