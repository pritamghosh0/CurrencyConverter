package com.example.currencyconverter.repository

import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.repository.remote.data.AllConversionRateResponse
import com.example.currencyconverter.repository.remote.data.AllCurrenciesResponse
import com.example.currencyconverter.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {

    // method to observe currency rate stored in database
    fun currencyRateDataDb(): Flow<CurrencyTable?>

    // method to insert latest currency rate to database
    suspend fun insert(currencyRate: CurrencyTable)

    // method to fetch latest exchange rate from api
    suspend fun getExchangeRates(appId: String): Flow<Resource<Response<AllConversionRateResponse>>>

    // method to fetch all currencies from api
    suspend fun getCurrencies(appId: String): Flow<Resource<Response<AllCurrenciesResponse>>>
}