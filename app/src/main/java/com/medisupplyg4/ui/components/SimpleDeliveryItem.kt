package com.medisupplyg4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.models.DeliveryPoint

@Composable
fun SimpleDeliveryItem(
    deliveryPoint: DeliveryPoint,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Imagen del lugar (placeholder con icono)
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Lugar",
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }

        // Información del lugar
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = deliveryPoint.customer.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Entrega #${deliveryPoint.orderId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Indicadores y flecha
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Indicador de cadena de frío si aplica
            if (deliveryPoint.requiresColdChain) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Cadena de frío",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Flecha
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
        }
    }
}
