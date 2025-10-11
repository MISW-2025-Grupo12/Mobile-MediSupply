package com.medisupplyg4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.models.SimpleDelivery
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ScrollAwareDateSelector(
    deliveries: List<SimpleDelivery>,
    selectedPeriod: RoutePeriod,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    
    // Determinar qué fecha mostrar según el período y el scroll
    val displayDate = when (selectedPeriod) {
        RoutePeriod.DAY -> {
            // Para día, siempre mostrar hoy
            today
        }
        RoutePeriod.WEEK, RoutePeriod.MONTH -> {
            // Para semana y mes, determinar la fecha basada en el scroll
            val visibleDate = getVisibleDateFromScroll(deliveries, listState)
            visibleDate ?: deliveries.firstOrNull()?.fechaEntrega?.toLocalDate() ?: today
        }
    }
    
    val isToday = displayDate == today
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mostrar "Hoy" solo si la fecha mostrada es hoy
        if (isToday) {
            Text(
                text = "Hoy",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            // Espacio vacío para mantener la alineación
            Spacer(modifier = Modifier.width(0.dp))
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = formatter.format(displayDate),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendario",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun getVisibleDateFromScroll(
    deliveries: List<SimpleDelivery>,
    listState: LazyListState
): LocalDate? {
    // Agrupar entregas por día
    val groupedDeliveries = deliveries
        .sortedBy { it.fechaEntrega }
        .groupBy { it.fechaEntrega.toLocalDate() }
        .toSortedMap()
    
    val dates = groupedDeliveries.keys.toList()
    
    // Calcular qué día está visible basado en la posición del scroll
    val firstVisibleIndex = listState.firstVisibleItemIndex
    val visibleItemCount = listState.layoutInfo.visibleItemsInfo.size
    
    // Cada día tiene un header + sus entregas
    var currentIndex = 0
    for ((date, dayDeliveries) in groupedDeliveries) {
        // Header del día cuenta como 1 item
        currentIndex += 1
        
        // Si el primer item visible está en este día
        if (firstVisibleIndex < currentIndex + dayDeliveries.size) {
            return date
        }
        
        currentIndex += dayDeliveries.size
    }
    
    return dates.firstOrNull()
}
