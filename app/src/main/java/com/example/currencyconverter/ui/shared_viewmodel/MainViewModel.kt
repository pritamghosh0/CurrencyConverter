package com.example.currencyconverter.ui.shared_viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.repository.Repository
import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.repository.remote.data.AllConversionRateResponse
import com.example.currencyconverter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val currencyRepository: Repository) : ViewModel() {

    val oldCurrencyRate : LiveData<CurrencyTable?> = currencyRepository.currencyRateDataDb().asLiveData()
    private val _selectedCurrency : MutableLiveData<String> = MutableLiveData("USD")
    val selectedCurrency : LiveData<String> get() = _selectedCurrency

    private val _currencyRate = MutableLiveData<Resource<Response<AllConversionRateResponse>>>()
    val currencyRate : LiveData<Resource<Response<AllConversionRateResponse>>> get() = _currencyRate

    fun updateSelectedCurrency(currency: String){
        _selectedCurrency.value = currency
    }

    fun insertCurrencyToDb(currencyData: CurrencyTable){
        viewModelScope.launch {
            currencyRepository.insert(currencyData)
        }
    }

    fun getCurrencies(appId: String) = liveData(Dispatchers.IO) {
        emit(currencyRepository.getCurrencies(appId))
    }

    // method to get exchange rate from api
    fun getExchangeRates(appId: String){
        viewModelScope.launch(Dispatchers.IO){
            currencyRepository.getExchangeRates(appId).collect{ resource->
                withContext(Dispatchers.Main) {
                    _currencyRate.value = resource
                }
            }
        }
    }
}

