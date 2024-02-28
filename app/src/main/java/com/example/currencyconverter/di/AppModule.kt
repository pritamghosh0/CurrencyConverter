package com.example.currencyconverter.di

import android.content.Context
import androidx.room.Room
import com.example.currencyconverter.repository.CurrencyConversionRepository
import com.example.currencyconverter.repository.Repository
import com.example.currencyconverter.repository.local.currency_db.CurrencyDao
import com.example.currencyconverter.repository.local.currency_db.CurrencyDatabase
import com.example.currencyconverter.repository.remote.ApiService
import com.example.currencyconverter.utils.CommonUtils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCurrencyDatabase(@ApplicationContext context: Context)
        = Room.databaseBuilder(context, CurrencyDatabase::class.java, "currency_converter_db").build()

    @Singleton
    @Provides
    fun provideCurrencyDao(database: CurrencyDatabase) = database.currencyDao()

    @Singleton
    @Provides
    fun provideCurrencyConversionRepository(dao: CurrencyDao, apiService: ApiService) = CurrencyConversionRepository(dao, apiService) as Repository

    @Singleton
    @Provides
    fun provideApiService(): ApiService{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)
    }
}