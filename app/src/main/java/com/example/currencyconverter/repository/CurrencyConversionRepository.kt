package com.example.currencyconverter.repository

import androidx.annotation.WorkerThread
import com.example.currencyconverter.repository.local.currency_db.CurrencyDao
import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.repository.remote.ApiService
import com.example.currencyconverter.repository.remote.data.AllConversionRateResponse
import com.example.currencyconverter.repository.remote.data.AllCurrenciesResponse
import com.example.currencyconverter.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class CurrencyConversionRepository @Inject constructor (private val currencyDao: CurrencyDao, private val apiService: ApiService) : Repository{


    override fun currencyRateDataDb(): Flow<CurrencyTable?> = currencyDao.getCurrencyData()

    // Room executes suspend queries off the main thread
    @WorkerThread
    override suspend fun insert(currencyRate: CurrencyTable){
        currencyDao.insertOrUpdateCurrencyData(currencyRate)
    }

    override suspend fun getExchangeRates(appId: String): Flow<Resource<Response<AllConversionRateResponse>>> = flow{
        try {
            emit(Resource.loading(data = null))
            val response = apiService.getExchangeRates(appId)
            emit(Resource.success(data = response))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    override suspend fun getCurrencies(appId: String): Flow<Resource<Response<AllCurrenciesResponse>>> = flow{
        try {
            emit(Resource.loading(data = null))
            val response = apiService.getCurrencies(appId)
            emit(Resource.success(data = response))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}