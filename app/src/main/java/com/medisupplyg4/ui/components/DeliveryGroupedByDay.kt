package com.medisupplyg4.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onDeliveryClick: (SimpleDelivery) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Agrupar entregas por día
    val groupedDeliveries = deliveries
        .sortedBy { it.fechaEntrega } // Ordenar por fecha de entrega (más antiguas primero)
        .groupBy { it.fechaEntrega.toLocalDate() }
        .toSortedMap(compareBy { it }) // Ordenar los días (más antiguos primero)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        groupedDeliveries.forEach { (date, dayDeliveries) ->
            // Header del día
            DayHeader(date = date)
            
            // Espaciado adicional
            Spacer(modifier = Modifier.height(8.dp))
            
            // Entregas del día (ordenadas por fecha ascendente - más antiguas primero)
            dayDeliveries.sortedBy { it.fechaEntrega }.forEach { delivery ->
                SimpleDeliveryCard(
                    delivery = delivery,
                    onClick = { onDeliveryClick(delivery) }
                )
                Spacer(modifier = Modifier.height(8.dp))
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
