package com.example.currencyconverter.repository.local.currency_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
    @ColumnInfo(name = "conversion_rate") val conversionRate: String
)