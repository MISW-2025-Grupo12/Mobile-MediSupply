package com.medisupplyg4.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.medisupplyg4.models.RoutePeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodTabs(
    selectedPeriod: RoutePeriod,
    onPeriodSelected: (RoutePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = listOf(
        RoutePeriod.DAY to "Día",
        RoutePeriod.WEEK to "Semana", 
        RoutePeriod.MONTH to "Mes"
    )

    TabRow(
        selectedTabIndex = periods.indexOfFirst { it.first == selectedPeriod },
        modifier = modifier
    ) {
        periods.forEachIndexed { index, (period, title) ->
            Tab(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
