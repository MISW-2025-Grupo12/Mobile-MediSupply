package com.medisupplyg4.network

import com.medisupplyg4.config.ApiConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente de red para configurar Retrofit
 */
object NetworkClient {
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    // Retrofit para el servicio de logística (entregas)
    private val logisticaRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.LOGISTICA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // Retrofit para el servicio de usuarios (vendedores)
    private val usuariosRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.USUARIOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // Retrofit para el servicio de ventas (visitas)
    private val ventasRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.VENTAS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val deliveryApiService: DeliveryApiService by lazy {
        logisticaRetrofit.create(DeliveryApiService::class.java)
    }
    
    val vendedorApiService: VendedorApiService by lazy {
        usuariosRetrofit.create(VendedorApiService::class.java)
    }
    
    val visitasApiService: VisitasApiService by lazy {
        ventasRetrofit.create(VisitasApiService::class.java)
    }
}
