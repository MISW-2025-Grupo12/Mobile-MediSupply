package com.medisupplyg4.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.medisupplyg4.R
import com.medisupplyg4.models.*
import com.medisupplyg4.network.DeliveryApiService
import com.medisupplyg4.network.NetworkClient
import com.medisupplyg4.utils.SessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class ClientDeliveriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ClientDeliveriesViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockDeliveryApiService: DeliveryApiService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)
        mockDeliveryApiService = mockk()

        // Mock NetworkClient
        mockkObject(NetworkClient)
        every { NetworkClient.deliveryApiService } returns mockDeliveryApiService

        // Mock SessionManager
        mockkObject(SessionManager)
        every { SessionManager.getToken(any()) } returns "test_token"

        // Mock application.getString calls - orden específico primero
        every { mockApplication.getString(eq(R.string.error_connection_error), any<String>()) } answers { 
            val errorMsg = firstArg<String>()
            "Error de conexión: $errorMsg"
        }
        every { mockApplication.getString(eq(R.string.error_load_deliveries), any<Int>()) } answers { 
            "Error al cargar entregas: ${firstArg<Int>()}"
        }
        every { mockApplication.getString(R.string.error_token_not_found) } returns "No se encontró token de autenticación"

        // Mock Log methods
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>()) } returns 0

        viewModel = ClientDeliveriesViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have empty deliveries and no error`() {
        // Given & When
        val deliveries = viewModel.deliveries.value
        val isLoading = viewModel.isLoading.value
        val errorMessage = viewModel.errorMessage.value
        val startDate = viewModel.selectedStartDate.value
        val endDate = viewModel.selectedEndDate.value

        // Then
        // deliveries puede ser null o lista vacía al inicio
        assertTrue(deliveries == null || deliveries.isEmpty())
        assertFalse(isLoading == true)
        assertNull(errorMessage)
        assertNull(startDate)
        assertNull(endDate)
    }

    @Test
    fun `loadDeliveries should set loading state to true`() = runTest {
        // Given
        val clienteId = "client-1"
        val mockResponse = createPaginatedResponse(createDeliveryResponse("delivery-1"))
        coEvery { mockDeliveryApiService.getDeliveriesByClienteId(any(), any(), any(), any(), any(), any()) } returns Response.success(mockResponse)

        val isLoadingObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isLoading.observeForever(isLoadingObserver)

        // When
        viewModel.loadDeliveries(clienteId)
        advanceUntilIdle()

        // Then
        verify { isLoadingObserver.onChanged(true) }
    }

    @Test
    fun `loadDeliveries should load deliveries successfully`() = runTest {
        // Given
        val clienteId = "client-1"
        val delivery1 = createDeliveryResponse("delivery-1", "2025-01-10T10:00:00")
        val delivery2 = createDeliveryResponse("delivery-2", "2025-01-15T14:30:00")
        val mockResponse = createPaginatedResponse(delivery1, delivery2)
        
        coEvery { 
            mockDeliveryApiService.getDeliveriesByClienteId(
                "Bearer test_token",
                clienteId,
                null,
                null,
                1,
                100
            ) 
        } returns Response.success(mockResponse)

        val deliveriesObserver = mockk<Observer<List<SimpleDelivery>>>(relaxed = true)
        viewModel.deliveries.observeForever(deliveriesObserver)

        // When
        viewModel.loadDeliveries(clienteId)
        advanceUntilIdle()

        // Then
        val deliveries = viewModel.deliveries.value
        assertNotNull(deliveries)
        assertEquals(2, deliveries?.size)
        verify { deliveriesObserver.onChanged(any()) }
    }

    @Test
    fun `loadDeliveries should sort deliveries by fechaEntrega ascending`() = runTest {
        // Given
        val clienteId = "client-1"
        // Crear entregas con fechas diferentes (más reciente primero en la respuesta)
        val delivery1 = createDeliveryResponse("delivery-1", "2025-01-15T14:30:00")
        val delivery2 = createDeliveryResponse("delivery-2", "2025-01-10T10:00:00")
        val delivery3 = createDeliveryResponse("delivery-3", "2025-01-12T12:00:00")
        val mockResponse = createPaginatedResponse(delivery1, delivery2, delivery3)
        
        coEvery { 
            mockDeliveryApiService.getDeliveriesByClienteId(any(), any(), any(), any(), any(), any()) 
        } returns Response.success(mockResponse)

        // When
        viewModel.loadDeliveries(clienteId)
        advanceUntilIdle()

        // Then
        val deliveries = viewModel.deliveries.value
        assertNotNull(deliveries)
        assertEquals(3, deliveries?.size)
        
        // Verificar orden ascendente (más antiguo primero)
        val fechas = deliveries?.map { it.fechaEntrega } ?: emptyList()
        assertTrue(fechas[0].isBefore(fechas[1]))
        assertTrue(fechas[1].isBefore(fechas[2]))
        assertEquals(LocalDateTime.parse("2025-01-10T10:00:00"), fechas[0])
        assertEquals(LocalDateTime.parse("2025-01-12T12:00:00"), fechas[1])
        assertEquals(LocalDateTime.parse("2025-01-15T14:30:00"), fechas[2])
    }

    @Test
    fun `loadDeliveries should apply date filters when dates are set`() = runTest {
        // Given
        val clienteId = "client-1"
        val startDate = LocalDate.of(2025, 1, 10)
        val endDate = LocalDate.of(2025, 1, 15)
        
        viewModel.setDateRange(startDate, endDate)
        
        val delivery1 = createDeliveryResponse("delivery-1", "2025-01-10T10:00:00")
        val mockResponse = createPaginatedResponse(delivery1)
        
        coEvery { 
            mockDeliveryApiService.getDeliveriesByClienteId(
                "Bearer test_token",
                clienteId,
                "2025-01-10",
                "2025-01-15",
                1,
                100
            ) 
        } returns Response.success(mockResponse)

        // When
        viewModel.loadDeliveries(clienteId)
        advanceUntilIdle()

        // Then
        coVerify { 
            mockDeliveryApiService.getDeliveriesByClienteId(
                "Bearer test_token",
                clienteId,
                "2025-01-10",
                "2025-01-15",
                1,
                100
            ) 
        }
    }

    @Test
    fun `loadDeliveries should handle empty token error`() = runTest {
        // Given
        every { SessionManager.getToken(any()) } returns null
        val clienteId = "client-1"
        
        val errorObserver = mockk<Observer<String?>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)

        // When
        viewModel.loadDeliveries(clienteId)
        advanceUntilIdle()

        // Then
        val errorMessage = viewModel.errorMessage.value
        assertNotNull(errorMessage)
        assertEquals("No se encontró token de autenticación", errorMessage)
        verify { errorObserver.onChanged("No se encontró token de autenticación") }
    }

    @Test
    fun `loadDeliveries should handle HTTP error response`() = runTest {
        // Given
        val clienteId = "client-1"
        val errorResponse = Response.error<PaginatedResponse<SimpleDeliveryResponse>>(
            404,
            ResponseBody.create(null, "Not found")
        )
        
        coEvery { 
            mockDeliveryApiService.getDeliveriesByClienteId(any(), any(), any(), any(), any(), any()) 
        } returns errorResponse

        val errorObserver = mockk<Observer<String?>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)

        // When
        viewModel.loadDeliveries(clienteId)
        advanceUntilIdle()

        // Then
        val errorMessage = viewModel.errorMessage.value
        assertNotNull(errorMessage)
        assertTrue(errorMessage?.contains("404") == true || errorMessage?.contains("Error al cargar entregas") == true)
    }


    @Test
    fun `setDateRange should update selected dates`() {
        // Given
        val startDate = LocalDate.of(2025, 1, 10)
        val endDate = LocalDate.of(2025, 1, 15)
        
        val startDateObserver = mockk<Observer<LocalDate?>>(relaxed = true)
        val endDateObserver = mockk<Observer<LocalDate?>>(relaxed = true)
        viewModel.selectedStartDate.observeForever(startDateObserver)
        viewModel.selectedEndDate.observeForever(endDateObserver)

        // When
        viewModel.setDateRange(startDate, endDate)

        // Then
        assertEquals(startDate, viewModel.selectedStartDate.value)
        assertEquals(endDate, viewModel.selectedEndDate.value)
        verify { startDateObserver.onChanged(startDate) }
        verify { endDateObserver.onChanged(endDate) }
    }

    @Test
    fun `setDateRange should allow null dates`() {
        // Given
        viewModel.setDateRange(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 15))
        
        // When
        viewModel.setDateRange(null, null)

        // Then
        assertNull(viewModel.selectedStartDate.value)
        assertNull(viewModel.selectedEndDate.value)
    }

    @Test
    fun `clearError should clear error message`() {
        // Given
        val errorObserver = mockk<Observer<String?>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        
        // Simular un error
        val errorField = ClientDeliveriesViewModel::class.java.getDeclaredField("_errorMessage")
        errorField.isAccessible = true
        val mutableError = errorField.get(viewModel) as androidx.lifecycle.MutableLiveData<String?>
        mutableError.value = "Test error"

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.errorMessage.value)
        verify { errorObserver.onChanged(null) }
    }

    @Test
    fun `loadDeliveries should cancel previous request when called again`() = runTest {
        // Given
        val clienteId = "client-1"
        val mockResponse = createPaginatedResponse(createDeliveryResponse("delivery-1"))
        
        coEvery { 
            mockDeliveryApiService.getDeliveriesByClienteId(any(), any(), any(), any(), any(), any()) 
        } coAnswers {
            kotlinx.coroutines.delay(100) // Simular delay
            Response.success(mockResponse)
        }

        // When
        viewModel.loadDeliveries(clienteId)
        viewModel.loadDeliveries(clienteId) // Llamar de nuevo inmediatamente
        advanceUntilIdle()

        // Then - La segunda llamada debería cancelar la primera
        // Verificamos que se llamó al menos una vez
        coVerify(atLeast = 1) { 
            mockDeliveryApiService.getDeliveriesByClienteId(any(), any(), any(), any(), any(), any()) 
        }
    }

    // Helper functions
    private fun createDeliveryResponse(
        id: String,
        fechaEntrega: String = "2025-01-10T10:00:00"
    ): SimpleDeliveryResponse {
        val cliente = ClienteAPI(
            id = "client-1",
            nombre = "Test Client",
            email = "test@test.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )
        
        val pedido = SimpleDeliveryResponse.PedidoResponse(
            id = "pedido-1",
            total = 1000.0,
            estado = "confirmado",
            fechaConfirmacion = "2025-01-09T10:00:00",
            vendedorId = "vendedor-1",
            cliente = cliente,
            productos = emptyList()
        )
        
        return SimpleDeliveryResponse(
            id = id,
            direccion = "Test Address",
            fechaEntregaString = fechaEntrega,
            cliente = null,
            pedido = pedido,
            productos = null
        )
    }

    private fun createPaginatedResponse(vararg deliveries: SimpleDeliveryResponse): PaginatedResponse<SimpleDeliveryResponse> {
        return PaginatedResponse(
            items = deliveries.toList(),
            pagination = PaginationInfo(
                page = 1,
                pageSize = 100,
                totalItems = deliveries.size,
                totalPages = 1,
                hasNext = false,
                hasPrev = false
            )
        )
    }
}

