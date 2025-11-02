package com.medisupplyg4.models

/**
 * Visual status for institutional client orders. UI will map these to icons/colors.
 */
enum class OrderStatus(val displayName: String) {
    BORRADOR("Borrador"),
    CONFIRMADO("Confirmado"),
    EN_TRANSITO("En tránsito"),
    ENTREGADO("Entregado");
}
