package com.medisupplyg4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.R
import com.medisupplyg4.models.OrderStatus

@Composable
fun OrderStatusChip(status: OrderStatus, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val statusText = stringResource(status.titleResId)
    
    val (bg, fg, iconRes) = when (status) {
        // Borrador
        OrderStatus.BORRADOR -> Triple(Color(0xFFFFF8E1), Color(0xFFF9A825), R.drawable.contract_edit)
        // Confirmado
        OrderStatus.CONFIRMADO -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), R.drawable.receipt_long)
        // En tránsito
        OrderStatus.EN_TRANSITO -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), R.drawable.local_shipping)
        // Entregado
        OrderStatus.ENTREGADO -> Triple(Color(0xFFE8F5E9), Color(0xFF1B5E20), R.drawable.check)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = statusText, tint = fg, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = statusText, color = fg, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}
