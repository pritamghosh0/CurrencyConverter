package com.example.currencyconverter.repository.local.currency_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrencyTable::class], version = 1)
public abstract class CurrencyDatabase : RoomDatabase(){
    abstract fun currencyDao(): CurrencyDao

    // switched to Hilt
    /*
    companion object{
        @Volatile
        private var INSTANCE: CurrencyDatabase? = null

        fun getDatabase(context: Context): CurrencyDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,
                    "currency_converter_db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    */
}