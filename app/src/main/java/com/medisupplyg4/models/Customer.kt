package com.medisupplyg4.models

data class Customer(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val email: String? = null,
    val type: CustomerType = CustomerType.MEDICAL_CENTER
)

enum class CustomerType {
    MEDICAL_CENTER,
    HOSPITAL,
    LABORATORY,
    CLINIC
}
