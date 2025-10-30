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

@ExperimentalCoroutinesApi
class ClientOrdersRepositoryTest {

    private lateinit var repository: ClientOrdersRepository
    private lateinit var mockApi: PedidosApiService

    @Before
    fun setUp() {
        // Mock Log methods to avoid not mocked errors
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

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
