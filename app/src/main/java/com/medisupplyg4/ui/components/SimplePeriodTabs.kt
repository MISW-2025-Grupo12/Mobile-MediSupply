package com.medisupplyg4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.models.RoutePeriod

@Composable
fun SimplePeriodTabs(
    selectedPeriod: RoutePeriod,
    onPeriodSelected: (RoutePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = listOf(
        RoutePeriod.DAY to "Día",
        RoutePeriod.WEEK to "Semana", 
        RoutePeriod.MONTH to "Mes"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        periods.forEach { (period, title) ->
            val isSelected = selectedPeriod == period
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPeriodSelected(period) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }
        }
    }
    
    // Línea separadora debajo de los tabs
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
