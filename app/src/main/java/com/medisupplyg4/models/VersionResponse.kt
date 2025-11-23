package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

data class VersionResponse(
    val version: String,
    @SerializedName("build_date") val buildDate: String,
    @SerializedName("commit_hash") val commitHash: String,
    val environment: String
)

