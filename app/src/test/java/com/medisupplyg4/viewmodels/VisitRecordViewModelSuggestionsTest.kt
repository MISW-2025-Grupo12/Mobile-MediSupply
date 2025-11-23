package com.medisupplyg4.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.medisupplyg4.R
import com.medisupplyg4.models.SugerenciaVisita
import com.medisupplyg4.models.VisitSuggestionsResponse
import com.medisupplyg4.repositories.SellerRepository
import com.medisupplyg4.utils.SessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class VisitRecordViewModelSuggestionsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: VisitRecordViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockRepository: SellerRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)
        mockRepository = mockk()

        // Mock SessionManager
        mockkObject(SessionManager)
        every { SessionManager.getToken(any()) } returns "test_token"

        // Mock application.getString calls
        every { mockApplication.getString(R.string.error_visit_id_not_available) } returns "Visita ID no disponible"
        every { mockApplication.getString(R.string.error_token_not_available) } returns "Token de autenticación no disponible"
        every { mockApplication.getString(R.string.error_get_suggestions) } returns "Error al obtener sugerencias"
        every { mockApplication.getString(R.string.error_connection_suggestions) } returns "Error de conexión al obtener sugerencias"

        // Mock Log methods
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0

        // Create ViewModel
        viewModel = VisitRecordViewModel(mockApplication)
        
        // Use reflection to inject mock repository
        val repositoryField = VisitRecordViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have no suggestions and not loading`() {
        // Given & When
        val suggestions = viewModel.suggestions.value
        val isLoading = viewModel.isLoadingSuggestions.value
        val error = viewModel.suggestionsError.value

        // Then
        assertNull(suggestions)
        assertFalse(isLoading == true)
        assertNull(error)
    }

    @Test
    fun `getVisitSuggestions should return error when visitaId is empty`() = runTest {
        // Given
        viewModel.setVisitData("", "client_id", "Client Name")

        // When
        viewModel.getVisitSuggestions()
        advanceUntilIdle()

        // Then
        val error = viewModel.suggestionsError.value
        assertEquals("Visita ID no disponible", error)
        assertFalse(viewModel.isLoadingSuggestions.value == true)
        assertNull(viewModel.suggestions.value)
    }

    @Test
    fun `getVisitSuggestions should return error when token is empty`() = runTest {
        // Given
        every { SessionManager.getToken(any()) } returns ""
        viewModel.setVisitData("visita_id", "client_id", "Client Name")

        // When
        viewModel.getVisitSuggestions()
        advanceUntilIdle()

        // Then
        val error = viewModel.suggestionsError.value
        assertEquals("Token de autenticación no disponible", error)
        assertFalse(viewModel.isLoadingSuggestions.value == true)
        assertNull(viewModel.suggestions.value)
    }

    @Test
    fun `getVisitSuggestions should load suggestions successfully`() = runTest {
        // Given
        val visitaId = "visita_id"
        val mockSuggestions = createMockSuggestionsResponse()
        viewModel.setVisitData(visitaId, "client_id", "Client Name")
        
        coEvery { 
            mockRepository.getVisitSuggestions("test_token", visitaId) 
        } returns mockSuggestions

        // When
        viewModel.getVisitSuggestions()
        advanceUntilIdle()

        // Then
        val suggestions = viewModel.suggestions.value
        assertNotNull(suggestions)
        assertEquals(mockSuggestions.visitaId, suggestions?.visitaId)
        assertEquals(mockSuggestions.mensaje, suggestions?.mensaje)
        assertEquals(mockSuggestions.sugerencia.id, suggestions?.sugerencia?.id)
        assertFalse(viewModel.isLoadingSuggestions.value == true)
        assertNull(viewModel.suggestionsError.value)
    }

    @Test
    fun `getVisitSuggestions should handle repository returning null`() = runTest {
        // Given
        val visitaId = "visita_id"
        viewModel.setVisitData(visitaId, "client_id", "Client Name")
        
        coEvery { 
            mockRepository.getVisitSuggestions("test_token", visitaId) 
        } returns null

        // When
        viewModel.getVisitSuggestions()
        advanceUntilIdle()

        // Then
        val error = viewModel.suggestionsError.value
        assertEquals("Error al obtener sugerencias", error)
        assertNull(viewModel.suggestions.value)
        assertFalse(viewModel.isLoadingSuggestions.value == true)
    }

    @Test
    fun `getVisitSuggestions should handle exception`() = runTest {
        // Given
        val visitaId = "visita_id"
        viewModel.setVisitData(visitaId, "client_id", "Client Name")
        
        coEvery { 
            mockRepository.getVisitSuggestions("test_token", visitaId) 
        } throws Exception("Network error")

        // When
        viewModel.getVisitSuggestions()
        advanceUntilIdle()

        // Then
        val error = viewModel.suggestionsError.value
        assertEquals("Error de conexión al obtener sugerencias", error)
        assertNull(viewModel.suggestions.value)
        assertFalse(viewModel.isLoadingSuggestions.value == true)
    }

    @Test
    fun `clearSuggestions should reset suggestions and error`() {
        // Given - set some state first
        viewModel.setVisitData("visita_id", "client_id", "Client Name")
        
        // When
        viewModel.clearSuggestions()

        // Then
        assertNull(viewModel.suggestions.value)
        assertNull(viewModel.suggestionsError.value)
    }

    private fun createMockSuggestionsResponse(): VisitSuggestionsResponse {
        return VisitSuggestionsResponse(
            mensaje = "Sugerencias generadas exitosamente",
            visitaId = "visita_id",
            sugerencia = SugerenciaVisita(
                id = "sugerencia_id",
                clienteId = "client_id",
                evidenciaId = null,
                sugerenciasTexto = "**PRODUCTOS RECOMENDADOS:**\n1. Producto A\n2. Producto B",
                modeloUsado = "gemini-2.5-flash-lite",
                createdAt = "2025-11-11T04:25:35.586258"
            )
        )
    }
}

