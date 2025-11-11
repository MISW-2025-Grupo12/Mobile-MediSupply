package com.medisupplyg4.models

import com.medisupplyg4.R

/**
 * Visual status for institutional client orders. UI will map these to icons/colors.
 */
enum class OrderStatus(val titleResId: Int) {
    BORRADOR(R.string.order_status_draft),
    CONFIRMADO(R.string.order_status_confirmed),
    EN_TRANSITO(R.string.order_status_in_transit),
    ENTREGADO(R.string.order_status_delivered);
}
