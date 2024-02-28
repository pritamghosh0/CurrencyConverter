package com.example.currencyconverter.repository.local.currency_db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CurrencyDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: CurrencyDatabase
    private lateinit var currencyDao: CurrencyDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), CurrencyDatabase::class.java
        ).allowMainThreadQueries().build()
        currencyDao = database.currencyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun insertCurrencyData() = runTest {
        val currencyItem = CurrencyTable(2137834480L, "USD:23.40")
        launch {
            currencyDao.insertCurrencyData(currencyItem)
        }
        advanceUntilIdle()
        val topCurrencyItem = currencyDao.getCurrencyData().first() as CurrencyTable

        assertEquals(topCurrencyItem, currencyItem)
    }

    @Test
    fun deleteAllCurrencies() = runTest {
        val currencyItem = CurrencyTable(2137834480L, "USD:23.40")
        launch {
            currencyDao.insertCurrencyData(currencyItem)
        }
        launch {
            currencyDao.deleteAllCurrencies()
        }
        advanceUntilIdle()
        val topCurrencyItem = currencyDao.getCurrencyData().first()

        assertNotEquals(topCurrencyItem, currencyItem)
    }

    @Test
    fun insertOrUpdateCurrencyData_Replaces_Previous_Data_With_New_One() = runTest {
        val currencyItem1 = CurrencyTable(2137834480L, "USD:23.40")
        val currencyItem2 = CurrencyTable(2137835260L, "ALL:48.52")
        launch {
            currencyDao.insertOrUpdateCurrencyData(currencyItem1)
        }
        launch {
            currencyDao.insertOrUpdateCurrencyData(currencyItem2)
        }
        advanceUntilIdle()

        val topCurrencyItem = currencyDao.getCurrencyData().first()

        assertEquals(topCurrencyItem, currencyItem2)
    }
}