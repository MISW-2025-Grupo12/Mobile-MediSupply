package com.medisupplyg4.config

/**
 * Configuración de la API del backend
 */
object ApiConfig {
    
    // TODO: Cambiar por las URLs reales del backend
    // URLs base para diferentes microservicios
    
    // Servicio de logística (entregas)
    const val LOGISTICA_BASE_URL = "http://10.0.2.2:5003/logistica/api/" // Para emulador Android
    // const val LOGISTICA_BASE_URL = "http://localhost:5003/logistica/api/" // Para dispositivo físico
    
    // Servicio de usuarios (vendedores)
    const val USUARIOS_BASE_URL = "http://10.0.2.2:5001/" // Para emulador Android
    // const val USUARIOS_BASE_URL = "http://localhost:5001/" // Para dispositivo físico
    
    // Servicio de ventas (visitas)
    const val VENTAS_BASE_URL = "http://10.0.2.2:5002/" // Para emulador Android
    // const val VENTAS_BASE_URL = "http://localhost:5002/" // Para dispositivo físico
    
    // Headers comunes
    const val CONTENT_TYPE = "application/json" // TODO: Mover a strings.xml
    const val ACCEPT = "application/json" // TODO: Mover a strings.xml
    
    // Timeouts
    const val CONNECT_TIMEOUT = 30L // segundos
    const val READ_TIMEOUT = 30L // segundos
    const val WRITE_TIMEOUT = 30L // segundos
}
