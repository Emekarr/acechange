package com.example.aceexchangerateapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.aceexchangerateapp.database.CurrencyDatabase
import com.example.aceexchangerateapp.database.CurrencyEntity
import com.example.aceexchangerateapp.database.transformToRecyclerViewObject
import com.example.aceexchangerateapp.networking.network_dto.ExchangeDto
import com.example.aceexchangerateapp.networking.network_dto.ExchangeRates
import com.example.aceexchangerateapp.networking.retrofit_api.Retrofit
import com.example.aceexchangerateapp.screens.recyclerview.RecyclerViewObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.reflect.full.memberProperties

//repository
class AppRepository(
    currencyDatabaseCompanion: CurrencyDatabase.Companion,
    androidContext: Context
) {
    private val currencyDatabase = currencyDatabaseCompanion.getInstance(androidContext)
    private val retrofitApi = Retrofit.retrofitApi

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val currencyList = MutableLiveData<List<CurrencyEntity>>()
    val recyclerList: LiveData<List<RecyclerViewObject>> = Transformations.map(currencyList){
        it.transformToRecyclerViewObject()
    }
    val baseCurrency = MutableLiveData<String>()


    fun getAllRates() {
        ioScope.launch {
            val exchangeDto = retrofitApi.getAllRates().await()
            withContext(Main){
                baseCurrency.value = exchangeDto.base
            }
            getDataFromExchangeDto(exchangeDto)
        }
    }

    private fun getDataFromExchangeDto(exchangeDto: ExchangeDto) {
        val listOfExchangeRates = mutableListOf<CurrencyEntity>()
        for (rate in ExchangeRates::class.memberProperties){
            listOfExchangeRates.add(CurrencyEntity(currency  = rate.name, value = rate.get(exchangeDto.rates) as Float))
        }
        cacheResults(listOfExchangeRates)
    }

    private fun cacheResults(listOfExchangeRates: MutableList<CurrencyEntity>) {
        ioScope.launch {
            currencyDatabase.currencyDao().cacheResults(*listOfExchangeRates.toTypedArray())
            withContext(Main){
                getCachedRates()
            }
            job.cancel()
        }
    }

    fun getCachedRates() {
        currencyDatabase.currencyDao().getCache().observeForever { list ->
            currencyList.value = list
        }
    }

}