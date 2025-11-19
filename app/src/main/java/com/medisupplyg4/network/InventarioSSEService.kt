package com.medisupplyg4.network

import android.util.Log
import com.medisupplyg4.config.ApiConfig
import com.medisupplyg4.models.InventarioSSEEvent
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

/**
 * Service to handle Server-Sent Events (SSE) for real-time inventory updates
 */
class InventarioSSEService {
    
    companion object {
        private const val TAG = "InventarioSSEService"
    }
    
    private val gson = Gson()
    private var eventSource: EventSource? = null
    
    /**
     * Event types from SSE stream
     */
    enum class EventType {
        INVENTORY,  // Initial inventory state
        UPDATE,     // Inventory updates
        HEARTBEAT   // Connection keep-alive
    }
    
    /**
     * Data class to represent SSE events
     */
    data class InventoryEvent(
        val type: EventType,
        val data: InventarioSSEEvent?
    )
    
    /**
     * Connects to the inventory SSE stream and returns a Flow of inventory events
     * @param token Authentication token
     * @return Flow of InventoryEvent
     */
    fun connect(token: String): Flow<InventoryEvent> = callbackFlow {
        val client = OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS) // No timeout for SSE streams
            .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
        
        val request = Request.Builder()
            .url("${ApiConfig.LOGISTICA_BASE_URL}inventario/stream")
            .header("Authorization", "Bearer $token")
            .header("Accept", "text/event-stream")
            .get()
            .build()
        
        val factory = EventSources.createFactory(client)
        
        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d(TAG, "SSE connection opened")
                this@InventarioSSEService.eventSource = eventSource
            }
            
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                try {
                    Log.d(TAG, "SSE event received: type=$type, data=$data")
                    
                    when (type) {
                        "inventory" -> {
                            val eventData = gson.fromJson(data, InventarioSSEEvent::class.java)
                            trySend(InventoryEvent(EventType.INVENTORY, eventData))
                        }
                        "update" -> {
                            val eventData = gson.fromJson(data, InventarioSSEEvent::class.java)
                            trySend(InventoryEvent(EventType.UPDATE, eventData))
                        }
                        "heartbeat" -> {
                            Log.d(TAG, "Heartbeat received")
                            trySend(InventoryEvent(EventType.HEARTBEAT, null))
                        }
                        else -> {
                            Log.w(TAG, "Unknown event type: $type")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing SSE event: ${e.message}", e)
                }
            }
            
            override fun onClosed(eventSource: EventSource) {
                Log.d(TAG, "SSE connection closed")
                close()
            }
            
            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                Log.e(TAG, "SSE connection failure: ${t?.message}", t)
                close(t ?: Exception("SSE connection failed"))
            }
        }
        
        eventSource = factory.newEventSource(request, listener)
        
        awaitClose {
            Log.d(TAG, "Closing SSE connection")
            eventSource?.cancel()
            eventSource = null
        }
    }
    
    /**
     * Disconnects from the SSE stream
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting SSE")
        eventSource?.cancel()
        eventSource = null
    }
}

