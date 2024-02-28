package com.example.currencyconverter.repository

import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.repository.remote.data.AllConversionRateResponse
import com.example.currencyconverter.repository.remote.data.AllCurrenciesResponse
import com.example.currencyconverter.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeCurrencyRepository: Repository {

    private var data = CurrencyTable(0L, "conversionRatesMapJson")
    private var dataLatest = AllConversionRateResponse(timeStamp = 0L, rates = hashMapOf(), base = "", error = false, status = 200, message = "", description = "")
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean){
        shouldReturnNetworkError = value
    }

    private val currencyTableDb = flow<CurrencyTable?> {
        emit(data)
    }

    override fun currencyRateDataDb(): Flow<CurrencyTable?> = currencyTableDb

    override suspend fun insert(currencyRate: CurrencyTable) {
        data = currencyRate
    }

    override suspend fun getExchangeRates(appId: String): Flow<Resource<Response<AllConversionRateResponse>>> = flow {
        if(appId.isEmpty())
            emit(Resource.error(data = null, message = "App Id not found"))
        else if(shouldReturnNetworkError)
            emit(Resource.error(data = null, message = "Network unavailable"))
        else
            emit(Resource.success(data = Response.success(dataLatest)))
    }

    override suspend fun getCurrencies(appId: String): Flow<Resource<Response<AllCurrenciesResponse>>> {
        TODO("Not yet implemented")
        // No usage
    }
}