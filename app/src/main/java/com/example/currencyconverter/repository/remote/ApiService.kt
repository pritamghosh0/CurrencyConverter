package com.example.currencyconverter.repository.remote

import com.example.currencyconverter.repository.remote.data.AllConversionRateResponse
import com.example.currencyconverter.repository.remote.data.AllCurrenciesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("currencies.json")
    suspend fun getCurrencies(@Query("app_id") appId: String): Response<AllCurrenciesResponse>


    @GET("latest.json")
    suspend fun getExchangeRates(@Query("app_id") appId: String): Response<AllConversionRateResponse>
}