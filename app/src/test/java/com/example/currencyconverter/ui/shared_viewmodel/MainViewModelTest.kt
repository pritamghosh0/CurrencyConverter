package com.example.currencyconverter.ui.shared_viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.currencyconverter.repository.FakeCurrencyRepository
import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.repository.remote.data.AllConversionRateResponse
import com.example.currencyconverter.utils.Resource
import com.example.currencyconverter.utils.Status
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var currencyRepository: FakeCurrencyRepository

    private lateinit var mainViewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        currencyRepository = FakeCurrencyRepository()
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(currencyRepository)
    }

    /**
     * In kotlin we don't need to validate for empty parameter that are non-null
     * */

    @Test
    fun `insertCurrencyToDb inserts new currency rate to DB`()= runTest {
        val data = CurrencyTable(0L, "conversionRatesMapNewJson")
        launch {
            mainViewModel.insertCurrencyToDb(data)
        }
        advanceUntilIdle()
        assertEquals((currencyRepository.currencyRateDataDb()).first(), data)
    }

    @Test
    fun `getExchangeRates emits error for empty appId`() = runTest{
        launch {
            mainViewModel.getExchangeRates("")
        }
        advanceUntilIdle()
        val observer = Observer< Resource<Response<AllConversionRateResponse>>> {resource->
            assertEquals(resource.status, Status.ERROR)
        }
        mainViewModel.currencyRate.observeForever (observer)
        mainViewModel.currencyRate.removeObserver(observer)
    }

    @Test
    fun `getExchangeRates emits error in offline mode`() = runTest{
        currencyRepository.setShouldReturnNetworkError(true)
        launch {
            mainViewModel.getExchangeRates("app_id")
        }
        advanceUntilIdle()
        val observer = Observer< Resource<Response<AllConversionRateResponse>>> {resource->
            assertEquals(resource.status, Status.ERROR)
        }
        mainViewModel.currencyRate.observeForever (observer)
        mainViewModel.currencyRate.removeObserver(observer)
    }

    @Test
    fun `getExchangeRates emits success response for all valid cases`() = runTest{
        launch {
            mainViewModel.getExchangeRates("app_id")
        }
        advanceUntilIdle()
        val observer = Observer< Resource<Response<AllConversionRateResponse>>> {resource->
            assertEquals(resource.status, Status.SUCCESS)
        }
        mainViewModel.currencyRate.observeForever (observer)
        mainViewModel.currencyRate.removeObserver(observer)
    }


    @OptIn(DelicateCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.shutdown()
    }
}