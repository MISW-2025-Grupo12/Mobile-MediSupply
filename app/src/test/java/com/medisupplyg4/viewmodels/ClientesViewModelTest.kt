package com.medisupplyg4.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.repositories.ClientesRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ClientesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ClientesViewModel
    private lateinit var mockApplication: Application
    private val testDispatcher = StandardTestDispatcher()

    // Datos de prueba
    private val testClientes = listOf(
        ClienteAPI(
            id = "1",
            nombre = "Cliente Activo",
            email = "activo@test.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Calle 1 #1-1",
            estado = "ACTIVO"
        ),
        ClienteAPI(
            id = "2",
            nombre = "Cliente Inactivo",
            email = "inactivo@test.com",
            identificacion = "0987654321",
            telefono = "3007654321",
            direccion = "Calle 2 #2-2",
            estado = "INACTIVO"
        ),
        ClienteAPI(
            id = "3",
            nombre = "Otro Cliente Activo",
            email = "otro@test.com",
            identificacion = "1122334455",
            telefono = "3001122334",
            direccion = "Calle 3 #3-3",
            estado = "ACTIVO"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        mockApplication = mockk()
        
        // Mock application.getString calls
        every { mockApplication.getString(R.string.error_unknown) } returns "Unknown error"
        every { mockApplication.getString(R.string.error_connection_error, any()) } returns "Connection error: test"
        
        // Mock Log methods to avoid "not mocked" errors
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        
        viewModel = ClientesViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have empty search query and TODOS status`() {
        // Given & When
        val searchQuery = viewModel.searchQuery.value
        val selectedStatus = viewModel.selectedStatus.value

        // Then
        assertEquals("", searchQuery)
        assertEquals("TODOS", selectedStatus)
    }

    @Test
    fun `updateSearchQuery should update search query`() {
        // Given
        val testQuery = "test search"
        val searchObserver = mockk<Observer<String>>(relaxed = true)
        viewModel.searchQuery.observeForever(searchObserver)

        // When
        viewModel.updateSearchQuery(testQuery)

        // Then
        assertEquals(testQuery, viewModel.searchQuery.value)
        verify { searchObserver.onChanged(testQuery) }
    }

    @Test
    fun `updateSearchQuery should trim whitespace`() {
        // Given
        val testQuery = "  test search  "
        val expectedQuery = "test search"

        // When
        viewModel.updateSearchQuery(testQuery)

        // Then
        assertEquals(expectedQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `updateStatusFilter should update selected status`() {
        // Given
        val testStatus = "ACTIVO"
        val statusObserver = mockk<Observer<String>>(relaxed = true)
        viewModel.selectedStatus.observeForever(statusObserver)

        // When
        viewModel.updateStatusFilter(testStatus)

        // Then
        assertEquals(testStatus, viewModel.selectedStatus.value)
        verify { statusObserver.onChanged(testStatus) }
    }

    @Test
    fun `clearSearch should reset search query to empty`() {
        // Given
        viewModel.updateSearchQuery("test query")
        val searchObserver = mockk<Observer<String>>(relaxed = true)
        viewModel.searchQuery.observeForever(searchObserver)

        // When
        viewModel.clearSearch()

        // Then
        assertEquals("", viewModel.searchQuery.value)
        verify { searchObserver.onChanged("") }
    }

    @Test
    fun `clearStatusFilter should reset status to TODOS`() {
        // Given
        viewModel.updateStatusFilter("ACTIVO")
        val statusObserver = mockk<Observer<String>>(relaxed = true)
        viewModel.selectedStatus.observeForever(statusObserver)

        // When
        viewModel.clearStatusFilter()

        // Then
        assertEquals("TODOS", viewModel.selectedStatus.value)
        verify { statusObserver.onChanged("TODOS") }
    }

    @Test
    fun `clearError should clear error state`() {
        // Given
        val errorObserver = mockk<Observer<String?>>(relaxed = true)
        viewModel.error.observeForever(errorObserver)

        // When
        viewModel.clearError()

        // Then
        verify { errorObserver.onChanged(null) }
    }

    @Test
    fun `filteredClientes should return all clientes when status is TODOS`() {
        // Given
        viewModel.updateStatusFilter("TODOS")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateStatusFilter("TODOS")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(3, filteredClientes?.size)
        assertTrue(filteredClientes?.contains(testClientes[0]) == true)
        assertTrue(filteredClientes?.contains(testClientes[1]) == true)
        assertTrue(filteredClientes?.contains(testClientes[2]) == true)
    }

    @Test
    fun `filteredClientes should return only ACTIVO clientes when status is ACTIVO`() {
        // Given
        viewModel.updateStatusFilter("ACTIVO")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateStatusFilter("ACTIVO")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(2, filteredClientes?.size)
        assertTrue(filteredClientes?.all { it.estado == "ACTIVO" } == true)
    }

    @Test
    fun `filteredClientes should return only INACTIVO clientes when status is INACTIVO`() {
        // Given
        viewModel.updateStatusFilter("INACTIVO")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateStatusFilter("INACTIVO")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(1, filteredClientes?.size)
        assertTrue(filteredClientes?.all { it.estado == "INACTIVO" } == true)
    }

    @Test
    fun `filteredClientes should filter by search query`() {
        // Given
        viewModel.updateSearchQuery("Cliente Activo")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateSearchQuery("Cliente Activo")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(2, filteredClientes?.size)
        assertTrue(filteredClientes?.all { it.nombre.contains("Cliente Activo", ignoreCase = true) } == true)
    }

    @Test
    fun `filteredClientes should filter by NIT`() {
        // Given
        viewModel.updateSearchQuery("1234567890")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateSearchQuery("1234567890")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(1, filteredClientes?.size)
        assertEquals("1234567890", filteredClientes?.first()?.identificacion)
    }

    @Test
    fun `filteredClientes should combine search and status filters`() {
        // Given
        viewModel.updateSearchQuery("Cliente")
        viewModel.updateStatusFilter("ACTIVO")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateSearchQuery("Cliente")
        viewModel.updateStatusFilter("ACTIVO")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(2, filteredClientes?.size)
        assertTrue(filteredClientes?.all { 
            it.nombre.contains("Cliente", ignoreCase = true) && it.estado == "ACTIVO" 
        } == true)
    }

    @Test
    fun `filteredClientes should be sorted alphabetically by name`() {
        // Given
        viewModel.updateStatusFilter("TODOS")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateStatusFilter("TODOS")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(3, filteredClientes?.size)
        
        // Verificar orden alfabético
        val nombres = filteredClientes?.map { it.nombre } ?: emptyList()
        assertEquals(listOf("Cliente Activo", "Cliente Inactivo", "Otro Cliente Activo"), nombres)
    }

    @Test
    fun `isEmpty should be true when no clientes match filters`() {
        // Given
        viewModel.updateSearchQuery("NonExistentClient")
        val isEmptyObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isEmpty.observeForever(isEmptyObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateSearchQuery("NonExistentClient")

        // Then
        val isEmpty = viewModel.isEmpty.value
        assertTrue(isEmpty == true)
    }

    @Test
    fun `isEmpty should be false when clientes match filters`() {
        // Given
        viewModel.updateStatusFilter("ACTIVO")
        val isEmptyObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isEmpty.observeForever(isEmptyObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateStatusFilter("ACTIVO")

        // Then
        val isEmpty = viewModel.isEmpty.value
        assertTrue(isEmpty == false)
    }

    @Test
    fun `search should be case insensitive`() {
        // Given
        viewModel.updateSearchQuery("cliente activo")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateSearchQuery("cliente activo")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(2, filteredClientes?.size)
        assertTrue(filteredClientes?.all { 
            it.nombre.contains("cliente activo", ignoreCase = true) 
        } == true)
    }

    @Test
    fun `status filter should be case insensitive`() {
        // Given
        viewModel.updateStatusFilter("activo")
        val filteredObserver = mockk<Observer<List<ClienteAPI>>>(relaxed = true)
        viewModel.filteredClientes.observeForever(filteredObserver)

        // When - Simular que se cargaron los clientes
        val clientesField = ClientesViewModel::class.java.getDeclaredField("_clientes")
        clientesField.isAccessible = true
        clientesField.set(viewModel, androidx.lifecycle.MutableLiveData(testClientes))

        // Trigger filtering
        viewModel.updateStatusFilter("activo")

        // Then
        val filteredClientes = viewModel.filteredClientes.value
        assertEquals(2, filteredClientes?.size)
        assertTrue(filteredClientes?.all { it.estado.equals("activo", ignoreCase = true) } == true)
    }
}