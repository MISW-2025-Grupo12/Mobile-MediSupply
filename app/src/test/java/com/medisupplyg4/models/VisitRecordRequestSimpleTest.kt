package com.medisupplyg4.models

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VisitRecordRequestSimpleTest {

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
        assertTrue(json.contains("\"fecha_realizada\":\"2024-01-15\""))
        assertTrue(json.contains("\"hora_realizada\":\"14:30:00\""))
        assertTrue(json.contains("\"cliente_id\":\"test-client-id\""))
        assertTrue(json.contains("\"novedades\":\"Test notes\""))
        assertTrue(json.contains("\"pedido_generado\":true"))
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
        assertTrue(json.contains("\"novedades\":\"\""))
        assertTrue(json.contains("\"pedido_generado\":false"))
    }
}

class VisitRecordResponseSimpleTest {

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
        assertTrue(json.contains("\"message\":\"Visita registrada exitosamente\""))
        assertTrue(json.contains("\"visita_id\":\"test-visit-id\""))
        assertTrue(json.contains("\"estado\":\"completada\""))
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
