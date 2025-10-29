package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent pagination information from API responses
 */
data class PaginationInfo(
    val page: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("has_next") val hasNext: Boolean,
    @SerializedName("has_prev") val hasPrev: Boolean
)
