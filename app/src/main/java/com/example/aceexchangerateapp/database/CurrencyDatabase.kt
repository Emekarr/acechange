package com.example.aceexchangerateapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrencyEntity::class], version = 1, exportSchema = false)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    companion object {
        private var INSTANCE: CurrencyDatabase? = null

        fun getInstance(context: Context): CurrencyDatabase {
            var instance = INSTANCE

            if (instance == null) {
                instance =
                    Room.databaseBuilder(context, CurrencyDatabase::class.java, "currency_database")
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}