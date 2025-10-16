package com.medisupplyg4.models

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VisitRecordRequestTest {

    @Test
    fun `VisitRecordRequest should serialize correctly with Gson`() {
        // Given
        val request = VisitRecordRequest(
            fechaRealizada = "2024-01-15",
            horaRealizada = "14:30:00",
            clienteId = "test-client-id",
            novedades = "Test notes",
            pedidoGenerado = true
        )
        
        // When
        val json = Gson().toJson(request)
        
        // Then
        assert(json.contains("\"fecha_realizada\":\"2024-01-15\""))
        assert(json.contains("\"hora_realizada\":\"14:30:00\""))
        assert(json.contains("\"cliente_id\":\"test-client-id\""))
        assert(json.contains("\"novedades\":\"Test notes\""))
        assert(json.contains("\"pedido_generado\":true"))
    }

    @Test
    fun `VisitRecordRequest should deserialize correctly from JSON`() {
        // Given
        val json = """
            {
                "fecha_realizada": "2024-01-15",
                "hora_realizada": "14:30:00",
                "cliente_id": "test-client-id",
                "novedades": "Test notes",
                "pedido_generado": true
            }
        """.trimIndent()
        
        // When
        val request = Gson().fromJson(json, VisitRecordRequest::class.java)
        
        // Then
        assertEquals("2024-01-15", request.fechaRealizada)
        assertEquals("14:30:00", request.horaRealizada)
        assertEquals("test-client-id", request.clienteId)
        assertEquals("Test notes", request.novedades)
        assertEquals(true, request.pedidoGenerado)
    }

    @Test
    fun `VisitRecordRequest should handle empty novedades`() {
        // Given
        val request = VisitRecordRequest(
            fechaRealizada = "2024-01-15",
            horaRealizada = "14:30:00",
            clienteId = "test-client-id",
            novedades = "",
            pedidoGenerado = false
        )
        
        // When
        val json = Gson().toJson(request)
        
        // Then
        assert(json.contains("\"novedades\":\"\""))
        assert(json.contains("\"pedido_generado\":false"))
    }
}

@RunWith(RobolectricTestRunner::class)
class VisitRecordResponseTest {

    @Test
    fun `VisitRecordResponse should serialize correctly with Gson`() {
        // Given
        val response = VisitRecordResponse(
            message = "Visita registrada exitosamente",
            visitaId = "test-visit-id",
            estado = "completada"
        )
        
        // When
        val json = Gson().toJson(response)
        
        // Then
        assert(json.contains("\"message\":\"Visita registrada exitosamente\""))
        assert(json.contains("\"visita_id\":\"test-visit-id\""))
        assert(json.contains("\"estado\":\"completada\""))
    }

    @Test
    fun `VisitRecordResponse should deserialize correctly from JSON`() {
        // Given
        val json = """
            {
                "message": "Visita registrada exitosamente",
                "visita_id": "test-visit-id",
                "estado": "completada"
            }
        """.trimIndent()
        
        // When
        val response = Gson().fromJson(json, VisitRecordResponse::class.java)
        
        // Then
        assertEquals("Visita registrada exitosamente", response.message)
        assertEquals("test-visit-id", response.visitaId)
        assertEquals("completada", response.estado)
    }
}
