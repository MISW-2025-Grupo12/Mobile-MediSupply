package com.medisupplyg4.models

/**
 * Generic model for paginated API responses
 */
data class PaginatedResponse<T>(
    val items: List<T>,
    val pagination: PaginationInfo
)

