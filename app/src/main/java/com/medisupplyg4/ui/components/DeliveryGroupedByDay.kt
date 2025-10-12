package com.medisupplyg4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.R
import com.medisupplyg4.utils.DateFormatter
import java.time.LocalDate

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
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        groupedDeliveries.forEach { (date, dayDeliveries) ->
            // Header del día
            item {
                DayHeader(date = date)
            }
            
            // Espaciado adicional para efecto de desprendimiento
            item {
                Spacer(modifier = Modifier.height(8.dp))
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
    val context = LocalContext.current
    val today = LocalDate.now()
    val isToday = date == today
    
    Card(
        modifier = modifier
            .fillMaxWidth()
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        Text(
            text = if (isToday) stringResource(R.string.today) else DateFormatter.formatLongDate(date, context),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurface
        )
        
            Text(
                text = DateFormatter.formatDayMonth(date, context),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
