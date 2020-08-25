package com.example.aceexchangerateapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheResults(vararg currencyEntity: CurrencyEntity)

    @Query("SELECT * FROM currency_entity")
    fun getCache(): LiveData<List<CurrencyEntity>>
}