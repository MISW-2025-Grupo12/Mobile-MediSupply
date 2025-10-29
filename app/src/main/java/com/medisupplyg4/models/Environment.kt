package com.medisupplyg4.models

/**
 * Enum que representa los diferentes ambientes de la aplicación
 */
enum class Environment(val displayNameResId: Int, val baseUrl: String) {
    DEVELOPMENT(
        displayNameResId = com.medisupplyg4.R.string.environment_development,
        baseUrl = "http://10.0.2.2:8080"
    ),
    PRODUCTION(
        displayNameResId = com.medisupplyg4.R.string.environment_production, 
        baseUrl = "https://api.medisupplyg4.online"
    );
    
    companion object {
        fun getDefault(): Environment = PRODUCTION
    }
}
