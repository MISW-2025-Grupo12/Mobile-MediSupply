package com.medisupplyg4.config

import com.medisupplyg4.models.Environment

/**
 * Configuración de la API del backend
 */
object ApiConfig {
    
    // Ambiente actual (por defecto producción)
    private var currentEnvironment: Environment = Environment.getDefault()
    
    // URLs base para diferentes microservicios
    val LOGISTICA_BASE_URL: String
        get() = when (currentEnvironment) {
            Environment.DEVELOPMENT -> "http://10.0.2.2:5003/logistica/api/"
            Environment.PRODUCTION -> "${currentEnvironment.baseUrl}/logistica/api/"
        }
    
    val USUARIOS_BASE_URL: String
        get() = when (currentEnvironment) {
            Environment.DEVELOPMENT -> "http://10.0.2.2:5001/"
            Environment.PRODUCTION -> "${currentEnvironment.baseUrl}/"
        }
    
    val VENTAS_BASE_URL: String
        get() = when (currentEnvironment) {
            Environment.DEVELOPMENT -> "http://10.0.2.2:5002/"
            Environment.PRODUCTION -> "${currentEnvironment.baseUrl}/"
        }
    
    val PRODUCTOS_BASE_URL: String
        get() = when (currentEnvironment) {
            Environment.DEVELOPMENT -> "http://10.0.2.2:5000/"
            Environment.PRODUCTION -> "${currentEnvironment.baseUrl}/"
        }

    // Timeouts
    const val CONNECT_TIMEOUT = 30L // segundos
    const val READ_TIMEOUT = 30L // segundos
    const val WRITE_TIMEOUT = 30L // segundos
    
    /**
     * Cambia el ambiente actual
     */
    fun setEnvironment(environment: Environment) {
        currentEnvironment = environment
    }
    
    /**
     * Obtiene el ambiente actual
     */
    fun getCurrentEnvironment(): Environment = currentEnvironment
}
