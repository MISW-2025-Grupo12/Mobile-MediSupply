package com.medisupplyg4.repositories

import android.content.Context
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.network.ClientesApiService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

@ExperimentalCoroutinesApi
class ClientesRepositoryTest {

    private lateinit var repository: ClientesRepository
    private lateinit var mockApiService: ClientesApiService
    private lateinit var mockContext: Context

    private val testClientes = listOf(
        ClienteAPI(
            id = "1",
            nombre = "Cliente Test 1",
            email = "test1@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Calle 1 #1-1",
            estado = "ACTIVO"
        ),
        ClienteAPI(
            id = "2",
            nombre = "Cliente Test 2",
            email = "test2@example.com",
            identificacion = "0987654321",
            telefono = "3007654321",
            direccion = "Calle 2 #2-2",
            estado = "INACTIVO"
        )
    )

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockContext = mockk()
        
        // Mock context.getString calls
        every { mockContext.getString(R.string.error_server_empty_response) } returns "Empty server response"
        every { mockContext.getString(R.string.error_server_error, any(), any()) } returns "Server error: %1\$d - %2\$s"
        
        // Mock Log methods to avoid "not mocked" errors
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        
        repository = ClientesRepository()
        
        // Usar reflection para inyectar el mock service
        val apiServiceField = ClientesRepository::class.java.getDeclaredField("clientesApiService")
        apiServiceField.isAccessible = true
        apiServiceField.set(repository, mockApiService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getClientes should return success when API call is successful`() = runTest {
        // Given
        val token = "test-token"
        val response = Response.success(testClientes)
        coEvery { mockApiService.getClientes("Bearer $token") } returns response

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testClientes, result.getOrNull())
    }

    @Test
    fun `getClientes should return failure when API call returns null body`() = runTest {
        // Given
        val token = "test-token"
        val response = Response.success<List<ClienteAPI>>(null)
        coEvery { mockApiService.getClientes("Bearer $token") } returns response

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals("Empty server response", exception?.message)
    }

    @Test
    fun `getClientes should return failure when API call throws exception`() = runTest {
        // Given
        val token = "test-token"
        val exception = RuntimeException("Network error")
        coEvery { mockApiService.getClientes("Bearer $token") } throws exception

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getClientes should format Bearer token correctly`() = runTest {
        // Given
        val token = "test-token"
        val response = Response.success(testClientes)
        coEvery { mockApiService.getClientes("Bearer $token") } returns response

        // When
        repository.getClientes(token, mockContext)

        // Then
        coVerify { mockApiService.getClientes("Bearer $token") }
    }

    @Test
    fun `getClientes should handle empty token`() = runTest {
        // Given
        val token = ""
        val response = Response.success(testClientes)
        coEvery { mockApiService.getClientes("Bearer ") } returns response

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isSuccess)
        coVerify { mockApiService.getClientes("Bearer ") }
    }

    @Test
    fun `getClientes should handle successful response with empty list`() = runTest {
        // Given
        val token = "test-token"
        val emptyList = emptyList<ClienteAPI>()
        val response = Response.success(emptyList)
        coEvery { mockApiService.getClientes("Bearer $token") } returns response

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
    }

    @Test
    fun `getClientes should handle successful response with single cliente`() = runTest {
        // Given
        val token = "test-token"
        val singleCliente = listOf(testClientes.first())
        val response = Response.success(singleCliente)
        coEvery { mockApiService.getClientes("Bearer $token") } returns response

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(singleCliente, result.getOrNull())
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getClientes should handle successful response with multiple clientes`() = runTest {
        // Given
        val token = "test-token"
        val response = Response.success(testClientes)
        coEvery { mockApiService.getClientes("Bearer $token") } returns response

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testClientes, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getClientes should handle timeout exception`() = runTest {
        // Given
        val token = "test-token"
        val timeoutException = java.net.SocketTimeoutException("Timeout")
        coEvery { mockApiService.getClientes("Bearer $token") } throws timeoutException

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isFailure)
        assertEquals(timeoutException, result.exceptionOrNull())
    }

    @Test
    fun `getClientes should handle connection exception`() = runTest {
        // Given
        val token = "test-token"
        val connectionException = java.net.ConnectException("Connection refused")
        coEvery { mockApiService.getClientes("Bearer $token") } throws connectionException

        // When
        val result = repository.getClientes(token, mockContext)

        // Then
        assertTrue(result.isFailure)
        assertEquals(connectionException, result.exceptionOrNull())
    }
}