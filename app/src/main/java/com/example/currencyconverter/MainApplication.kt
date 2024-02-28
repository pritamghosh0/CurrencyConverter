package com.example.currencyconverter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
       // appContext = applicationContext
    }

    // Singletons, now replaced by hilt provider
    /*
    companion object {
        lateinit var appContext: Context
        val database by lazy { CurrencyDatabase.getDatabase(appContext) }
        val repository by lazy { CurrencyConversionRepository(database.currencyDao()) }
        val apiService by lazy { RetrofitClient.apiInstance }
    }
    */

}