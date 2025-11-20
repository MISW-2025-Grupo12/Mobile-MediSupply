package com.medisupplyg4.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.medisupplyg4.models.OrderItemUI
import com.medisupplyg4.models.OrderStatus
import com.medisupplyg4.models.OrderUI
import com.medisupplyg4.repositories.SellerOrdersRepository
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
import java.time.LocalDate

@ExperimentalCoroutinesApi
class SellerOrdersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SellerOrdersViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockRepository: SellerOrdersRepository
    private val testDispatcher = StandardTestDispatcher()

    private val testOrders = listOf(
        OrderUI(
            id = "order-3",
            number = "Pedido 003",
            createdAt = LocalDate.of(2025, 1, 15),
            status = OrderStatus.CONFIRMADO,
            items = listOf(
                OrderItemUI("item-1", "Product 1", 2, 50.0)
            )
        ),
        OrderUI(
            id = "order-1",
            number = "Pedido 001",
            createdAt = LocalDate.of(2025, 1, 10),
            status = OrderStatus.BORRADOR,
            items = listOf(
                OrderItemUI("item-2", "Product 2", 1, 100.0)
            )
        ),
        OrderUI(
            id = "order-2",
            number = "Pedido 002",
            createdAt = LocalDate.of(2025, 1, 12),
            status = OrderStatus.ENTREGADO,
            items = listOf(
                OrderItemUI("item-3", "Product 3", 3, 25.0)
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk()
        mockRepository = mockk()

        // Mock SessionManager
        mockkObject(SessionManager)
        every { SessionManager.getToken(any()) } returns "test_token"
        every { SessionManager.getUserId(any()) } returns "vendedor-1"

        // Mock Application getString
        every { mockApplication.getString(com.medisupplyg4.R.string.order_number_prefix) } returns "Pedido"

        // Mock Log methods
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>()) } returns 0

        // Mock repository using reflection since it's private
        viewModel = SellerOrdersViewModel(mockApplication)
        val repositoryField = SellerOrdersViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have empty orders and not loading`() {
        // Given & When
        val orders = viewModel.orders.value
        val filteredOrders = viewModel.filteredOrders.value
        val isLoading = viewModel.isLoading.value
        val startDate = viewModel.selectedStartDate.value
        val endDate = viewModel.selectedEndDate.value

        // Then
        assertTrue(orders?.isEmpty() == true)
        assertTrue(filteredOrders?.isEmpty() == true)
        assertFalse(isLoading == true)
        // Should have default week filter set (Monday to Sunday of current week)
        assertNotNull(startDate)
        assertNotNull(endDate)
    }

    @Test
    fun `initial state should set default week filter`() {
        // Given & When
        val startDate = viewModel.selectedStartDate.value
        val endDate = viewModel.selectedEndDate.value

        // Then
        assertNotNull(startDate)
        assertNotNull(endDate)
        // Start date should be Monday
        assertEquals(java.time.DayOfWeek.MONDAY, startDate?.dayOfWeek)
        // End date should be Sunday
        assertEquals(java.time.DayOfWeek.SUNDAY, endDate?.dayOfWeek)
        // End date should be after start date
        assertTrue(endDate!!.isAfter(startDate) || endDate.isEqual(startDate))
    }

    @Test
    fun `loadOrders should sort orders by createdAt ascending`() = runTest {
        // Given
        // Los pedidos vienen desordenados (más reciente primero)
        val unorderedOrders = listOf(testOrders[0], testOrders[1], testOrders[2])
        
        coEvery { 
            mockRepository.getPedidosVendedor(any(), any(), any(), any(), any()) 
        } returns Result.success(unorderedOrders)

        val ordersObserver = mockk<Observer<List<OrderUI>>>(relaxed = true)
        viewModel.orders.observeForever(ordersObserver)

        // When
        viewModel.loadOrders()
        advanceUntilIdle()

        // Then
        val orders = viewModel.orders.value
        assertNotNull(orders)
        assertEquals(3, orders?.size)
        
        // Verificar orden ascendente (más antiguo primero)
        val fechas = orders?.map { it.createdAt } ?: emptyList()
        assertTrue(fechas[0].isBefore(fechas[1]))
        assertTrue(fechas[1].isBefore(fechas[2]))
        assertEquals(LocalDate.of(2025, 1, 10), fechas[0])
        assertEquals(LocalDate.of(2025, 1, 12), fechas[1])
        assertEquals(LocalDate.of(2025, 1, 15), fechas[2])
    }

    @Test
    fun `applyDateFilter should filter orders by date range`() {
        // Given
        val ordersField = SellerOrdersViewModel::class.java.getDeclaredField("_orders")
        ordersField.isAccessible = true
        val mutableOrders = ordersField.get(viewModel) as androidx.lifecycle.MutableLiveData<List<OrderUI>>
        mutableOrders.value = testOrders.sortedBy { it.createdAt }

        val startDate = LocalDate.of(2025, 1, 11)
        val endDate = LocalDate.of(2025, 1, 14)

        val filteredObserver = mockk<Observer<List<OrderUI>>>(relaxed = true)
        viewModel.filteredOrders.observeForever(filteredObserver)

        // When
        viewModel.setDateRange(startDate, endDate)

        // Then
        val filtered = viewModel.filteredOrders.value
        assertNotNull(filtered)
        assertEquals(1, filtered?.size)
        assertEquals("Pedido 002", filtered?.first()?.number)
        assertTrue(filtered?.first()?.createdAt?.isAfter(startDate.minusDays(1)) == true)
        assertTrue(filtered?.first()?.createdAt?.isBefore(endDate.plusDays(1)) == true)
    }

    @Test
    fun `applyDateFilter should filter orders from start date only`() {
        // Given
        val ordersField = SellerOrdersViewModel::class.java.getDeclaredField("_orders")
        ordersField.isAccessible = true
        val mutableOrders = ordersField.get(viewModel) as androidx.lifecycle.MutableLiveData<List<OrderUI>>
        mutableOrders.value = testOrders.sortedBy { it.createdAt }

        val startDate = LocalDate.of(2025, 1, 12)

        // When
        viewModel.setDateRange(startDate, null)

        // Then
        val filtered = viewModel.filteredOrders.value
        assertNotNull(filtered)
        assertEquals(2, filtered?.size)
        assertTrue(filtered?.all { !it.createdAt.isBefore(startDate) } == true)
    }

    @Test
    fun `applyDateFilter should return all orders when no date filter is set`() {
        // Given
        val ordersField = SellerOrdersViewModel::class.java.getDeclaredField("_orders")
        ordersField.isAccessible = true
        val mutableOrders = ordersField.get(viewModel) as androidx.lifecycle.MutableLiveData<List<OrderUI>>
        mutableOrders.value = testOrders.sortedBy { it.createdAt }

        // When
        viewModel.setDateRange(null, null)

        // Then
        val filtered = viewModel.filteredOrders.value
        assertNotNull(filtered)
        assertEquals(3, filtered?.size)
    }

    @Test
    fun `applyDateFilter should maintain ascending order after filtering`() {
        // Given
        val ordersField = SellerOrdersViewModel::class.java.getDeclaredField("_orders")
        ordersField.isAccessible = true
        val mutableOrders = ordersField.get(viewModel) as androidx.lifecycle.MutableLiveData<List<OrderUI>>
        mutableOrders.value = testOrders.sortedBy { it.createdAt }

        val startDate = LocalDate.of(2025, 1, 10)
        val endDate = LocalDate.of(2025, 1, 15)

        // When
        viewModel.setDateRange(startDate, endDate)

        // Then
        val filtered = viewModel.filteredOrders.value
        assertNotNull(filtered)
        assertEquals(3, filtered?.size)
        
        // Verificar que mantiene orden ascendente
        val fechas = filtered?.map { it.createdAt } ?: emptyList()
        assertTrue(fechas[0].isBefore(fechas[1]))
        assertTrue(fechas[1].isBefore(fechas[2]))
    }

    @Test
    fun `setDate should set start date and clear end date`() {
        // Given
        val date = LocalDate.of(2025, 1, 12)
        
        val startDateObserver = mockk<Observer<LocalDate?>>(relaxed = true)
        val endDateObserver = mockk<Observer<LocalDate?>>(relaxed = true)
        viewModel.selectedStartDate.observeForever(startDateObserver)
        viewModel.selectedEndDate.observeForever(endDateObserver)

        // When
        viewModel.setDate(date)

        // Then
        assertEquals(date, viewModel.selectedStartDate.value)
        assertNull(viewModel.selectedEndDate.value)
        verify { startDateObserver.onChanged(date) }
        verify { endDateObserver.onChanged(null) }
    }

    @Test
    fun `setDateRange should update both dates`() {
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
    fun `clearDateFilter should reset to default week filter`() {
        // Given
        val customStart = LocalDate.of(2025, 1, 1)
        val customEnd = LocalDate.of(2025, 1, 31)
        viewModel.setDateRange(customStart, customEnd)
        
        // When
        viewModel.clearDateFilter()
        
        // Then
        val startDate = viewModel.selectedStartDate.value
        val endDate = viewModel.selectedEndDate.value
        assertNotNull(startDate)
        assertNotNull(endDate)
        // Should be reset to current week (Monday to Sunday)
        assertEquals(java.time.DayOfWeek.MONDAY, startDate?.dayOfWeek)
        assertEquals(java.time.DayOfWeek.SUNDAY, endDate?.dayOfWeek)
    }

    @Test
    fun `loadOrders should handle repository failure gracefully`() = runTest {
        // Given
        val exception = Exception("Repository error")
        coEvery { 
            mockRepository.getPedidosVendedor(any(), any(), any(), any(), any()) 
        } returns Result.failure(exception)

        val ordersObserver = mockk<Observer<List<OrderUI>>>(relaxed = true)
        viewModel.orders.observeForever(ordersObserver)

        // When
        viewModel.loadOrders()
        advanceUntilIdle()

        // Then
        val orders = viewModel.orders.value
        assertNotNull(orders)
        assertTrue(orders?.isEmpty() == true)
    }

    @Test
    fun `loadOrders should set loading state correctly`() = runTest {
        // Given
        coEvery { 
            mockRepository.getPedidosVendedor(any(), any(), any(), any(), any()) 
        } returns Result.success(emptyList())

        val isLoadingObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isLoading.observeForever(isLoadingObserver)

        // When
        viewModel.loadOrders()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isLoading.value == true)
        verify { isLoadingObserver.onChanged(true) }
        verify { isLoadingObserver.onChanged(false) }
    }
}

