package com.medisupplyg4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.models.SimpleDelivery
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DeliveryGroupedByDay(
    deliveries: List<SimpleDelivery>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    // Agrupar entregas por día
    val groupedDeliveries = deliveries
        .sortedBy { it.fechaEntrega } // Ordenar por fecha de entrega
        .groupBy { it.fechaEntrega.toLocalDate() }
        .toSortedMap() // Ordenar los días

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedDeliveries.forEach { (date, dayDeliveries) ->
            // Header del día
            item {
                DayHeader(date = date)
            }
            
            // Entregas del día
            items(dayDeliveries) { delivery ->
                SimpleDeliveryCard(delivery = delivery)
            }
        }
    }
}

@Composable
private fun DayHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val today = LocalDate.now()
    val isToday = date == today
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                       else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isToday) "Hoy" else formatter.format(date).replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isToday) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "${date.dayOfMonth}/${date.monthValue}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isToday) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
