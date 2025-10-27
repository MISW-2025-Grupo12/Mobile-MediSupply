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
    
    private var okHttpClient: OkHttpClient? = null
    private var logisticaRetrofit: Retrofit? = null
    private var usuariosRetrofit: Retrofit? = null
    private var ventasRetrofit: Retrofit? = null
    private var productosRetrofit: Retrofit? = null
    private var clientRegistrationRetrofit: Retrofit? = null
    
    private fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build()
        }
        return okHttpClient!!
    }
    
    private fun getLogisticaRetrofit(): Retrofit {
        if (logisticaRetrofit == null) {
            logisticaRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.LOGISTICA_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return logisticaRetrofit!!
    }
    
    private fun getUsuariosRetrofit(): Retrofit {
        if (usuariosRetrofit == null) {
            usuariosRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.USUARIOS_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return usuariosRetrofit!!
    }
    
    private fun getVentasRetrofit(): Retrofit {
        if (ventasRetrofit == null) {
            ventasRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.VENTAS_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return ventasRetrofit!!
    }
    
    private fun getProductosRetrofit(): Retrofit {
        if (productosRetrofit == null) {
            productosRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.PRODUCTOS_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return productosRetrofit!!
    }
    
    private fun getClientRegistrationRetrofit(): Retrofit {
        if (clientRegistrationRetrofit == null) {
            clientRegistrationRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.CLIENT_REGISTRATION_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return clientRegistrationRetrofit!!
    }
    
    val deliveryApiService: DeliveryApiService
        get() = getLogisticaRetrofit().create(DeliveryApiService::class.java)
    
    val vendedorApiService: VendedorApiService
        get() = getUsuariosRetrofit().create(VendedorApiService::class.java)
    
    val visitasApiService: VisitasApiService
        get() = getVentasRetrofit().create(VisitasApiService::class.java)
    
    val clientesApiService: ClientesApiService
        get() = getUsuariosRetrofit().create(ClientesApiService::class.java)
    
    val productosApiService: ProductosApiService
        get() = getProductosRetrofit().create(ProductosApiService::class.java)
    
    val pedidosApiService: PedidosApiService
        get() = getVentasRetrofit().create(PedidosApiService::class.java)
    
    val inventarioApiService: InventarioApiService
        get() = getLogisticaRetrofit().create(InventarioApiService::class.java)
    
    val clientRegistrationApiService: ClientRegistrationApiService
        get() = getClientRegistrationRetrofit().create(ClientRegistrationApiService::class.java)

    val loginApiService: LoginApiService
        get() = getClientRegistrationRetrofit().create(LoginApiService::class.java)
    
    /**
     * Reinicializa todos los clientes de red cuando cambia el ambiente
     */
    fun resetClients() {
        okHttpClient = null
        logisticaRetrofit = null
        usuariosRetrofit = null
        ventasRetrofit = null
        productosRetrofit = null
        clientRegistrationRetrofit = null
    }
}
