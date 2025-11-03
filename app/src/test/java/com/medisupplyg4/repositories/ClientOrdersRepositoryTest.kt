package com.medisupplyg4.repositories

import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.PaginationInfo
import com.medisupplyg4.models.PedidoClienteAPI
import com.medisupplyg4.models.PedidoClienteItemAPI
import com.medisupplyg4.network.NetworkClient
import com.medisupplyg4.network.PedidosApiService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response
import java.time.LocalDate

@ExperimentalCoroutinesApi
class ClientOrdersRepositoryTest {

    private lateinit var repository: ClientOrdersRepository
    private lateinit var mockApi: PedidosApiService

    @Before
    fun setUp() {
        // Mock Log methods to avoid not mocked errors
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>(), any<Throwable>()) } returns 0

        mockApi = mockk()
        mockkObject(NetworkClient)
        every { NetworkClient.pedidosApiService } returns mockApi

        repository = ClientOrdersRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun createPaginatedResponse(vararg pedidos: PedidoClienteAPI): PaginatedResponse<PedidoClienteAPI> {
        val list = pedidos.toList()
        return PaginatedResponse(
            items = list,
            pagination = PaginationInfo(
                page = 1,
                pageSize = 20,
                totalItems = list.size,
                totalPages = 1,
                hasNext = false,
                hasPrev = false
            )
        )
    }

    @Test
    fun `getPedidosCliente returns mapped list on success`() = runTest {
        // Given
        val token = "tkn"
        val clienteId = "client-1"
        val apiResponse = createPaginatedResponse(
            PedidoClienteAPI(
                id = "abc123456",
                vendedorId = "vend-1",
                clienteId = clienteId,
                estado = "confirmado",
                total = 1000.0,
                fechaCreacion = "2025-10-29T02:06:19.651168",
                items = listOf(
                    PedidoClienteItemAPI(
                        id = "it1",
                        productoId = "p1",
                        nombreProducto = "Prod 1",
                        cantidad = 2,
                        precioUnitario = 50.0,
                        subtotal = 100.0
                    )
                )
            )
        )
        coEvery { mockApi.getPedidosCliente("Bearer $token", clienteId, 1, 20) } returns Response.success(apiResponse)

        // When
        val result = repository.getPedidosCliente(token, clienteId)

        // Then
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertNotNull(list)
        assertEquals(1, list!!.size)
        val first = list.first()
        assertEquals("Pedido abc123", first.number) // built from id.take(6)
        assertEquals(1, first.items.size)
        assertEquals("Prod 1", first.items.first().name)
        assertEquals(LocalDate.of(2025, 10, 29), first.createdAt)
    }

    @Test
    fun `getPedidosCliente correctly parses fecha_creacion from ISO format`() = runTest {
        // Given
        val token = "tkn"
        val clienteId = "client-1"
        val apiResponse = createPaginatedResponse(
            PedidoClienteAPI(
                id = "test123",
                vendedorId = "vend-1",
                clienteId = clienteId,
                estado = "entregado",
                total = 2000.0,
                fechaCreacion = "2025-09-15T10:30:45.123456",
                items = emptyList()
            )
        )
        coEvery { mockApi.getPedidosCliente("Bearer $token", clienteId, 1, 20) } returns Response.success(apiResponse)

        // When
        val result = repository.getPedidosCliente(token, clienteId)

        // Then
        assertTrue(result.isSuccess)
        val order = result.getOrNull()?.first()
        assertNotNull(order)
        assertEquals(LocalDate.of(2025, 9, 15), order!!.createdAt)
    }

    @Test
    fun `getPedidosCliente uses current date as fallback when fecha_creacion is invalid`() = runTest {
        // Given
        val token = "tkn"
        val clienteId = "client-1"
        val apiResponse = createPaginatedResponse(
            PedidoClienteAPI(
                id = "test123",
                vendedorId = "vend-1",
                clienteId = clienteId,
                estado = "borrador",
                total = 500.0,
                fechaCreacion = "invalid-date-format",
                items = emptyList()
            )
        )
        coEvery { mockApi.getPedidosCliente("Bearer $token", clienteId, 1, 20) } returns Response.success(apiResponse)

        // When
        val result = repository.getPedidosCliente(token, clienteId)

        // Then
        assertTrue(result.isSuccess)
        val order = result.getOrNull()?.first()
        assertNotNull(order)
        // Should fallback to current date (within today)
        val today = LocalDate.now()
        assertEquals(today, order!!.createdAt)
    }

    @Test
    fun `getPedidosCliente returns failure on HTTP error`() = runTest {
        // Given
        val token = "tkn"
        val clienteId = "client-1"
        coEvery { mockApi.getPedidosCliente("Bearer $token", clienteId, 1, 20) } returns Response.error(403, okhttp3.ResponseBody.create(null, ""))

        // When
        val result = repository.getPedidosCliente(token, clienteId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.startsWith("HTTP_") == true)
    }
}
