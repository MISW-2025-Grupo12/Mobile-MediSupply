package com.medisupplyg4.config

/**
 * Configuración de la API del backend
 */
object ApiConfig {
    
    // TODO: Cambiar por la URL real del backend
    // const val BASE_URL = "https://api.medisupply.com/"

    const val BASE_URL = "http://10.0.2.2:5003/api/logistica/" // Para emulador Android
    // const val BASE_URL = "http://localhost:5003/api/logistica/" // Para dispositivo físico
    
    // Headers comunes
    const val CONTENT_TYPE = "application/json" // TODO: Mover a strings.xml
    const val ACCEPT = "application/json" // TODO: Mover a strings.xml
    
    // Timeouts
    const val CONNECT_TIMEOUT = 30L // segundos
    const val READ_TIMEOUT = 30L // segundos
    const val WRITE_TIMEOUT = 30L // segundos
}
