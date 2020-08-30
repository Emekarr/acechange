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

    private val currencyList = MutableLiveData<List<CurrencyEntity>>()
    val recyclerList: LiveData<List<RecyclerViewObject>> = Transformations.map(currencyList) {
        it.transformToRecyclerViewObject()
    }

    val currencyToConvertList = MutableLiveData<List<CurrencyEntity>>()


    val baseCurrency = MutableLiveData<String>()


    fun getAllRates(baseCurrencyCall: String) {
        val job = Job()
        val ioScope = CoroutineScope(Dispatchers.IO + job)

        ioScope.launch {
            val exchangeDto = retrofitApi.getAllRates(baseCurrencyCall).await()
            withContext(Main) {
                baseCurrency.value = exchangeDto.base
            }
            getDataFromExchangeDto(exchangeDto, job)
        }
    }

    private fun getDataFromExchangeDto(exchangeDto: ExchangeDto, job: Job) {
        val listOfExchangeRates = mutableListOf<CurrencyEntity>()
        for (rate in ExchangeRates::class.memberProperties) {
            rate.get(exchangeDto.rates)?.let {
                listOfExchangeRates.add(CurrencyEntity(currency = rate.name, value = it as Float))
            }
        }
        job.cancel()
        cacheResults(listOfExchangeRates)
    }

    private fun cacheResults(listOfExchangeRates: MutableList<CurrencyEntity>) {
        val job = Job()
        val ioScope = CoroutineScope(Dispatchers.IO + job)

        ioScope.launch {
            currencyDatabase.currencyDao().cacheResults(*listOfExchangeRates.toTypedArray())
            withContext(Main) {
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

    fun getCurrenciesToConvert(currency1: String, currency2: String){
        val job = Job()
        val ioScope = CoroutineScope(Dispatchers.IO + job)

        ioScope.launch {
            val result = currencyDatabase.currencyDao().getCurrenciesForConverting(currency1, currency2)
            withContext(Main){
                currencyToConvertList.value = result
            }
        }
    }



}