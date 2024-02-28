package com.example.currencyconverter.repository.local.currency_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM CurrencyTable LIMIT 1")
    fun getCurrencyData(): Flow<CurrencyTable?>

    @Query("DELETE FROM CurrencyTable")
    suspend fun deleteAllCurrencies()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCurrencyData(currency: CurrencyTable)

    @Transaction
    suspend fun insertOrUpdateCurrencyData(currency: CurrencyTable) {
        deleteAllCurrencies()
        insertCurrencyData(currency)
    }
}