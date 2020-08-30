package com.example.aceexchangerateapp.screens.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.aceexchangerateapp.networking.BaseCurrencyLiveData
import com.example.aceexchangerateapp.repository.AppRepository
import com.example.aceexchangerateapp.screens.recyclerview.RecyclerViewObject

class RatesFragmentViewModel(private val appRepository: AppRepository) : ViewModel() {

    val recyclerList = MutableLiveData<List<RecyclerViewObject>>()
    val resultRate = MutableLiveData<String>()

    init {
        getAndObserveRecyclerListAndBaseCurrencyFromRepo()
        monitorResultsLiveData()
    }

    fun getAllRates(baseCurrencyCall: String) {
        appRepository.getAllRates(baseCurrencyCall)
    }

    private fun getAndObserveRecyclerListAndBaseCurrencyFromRepo() {
        val recyclerViewObserver =
            Observer<List<RecyclerViewObject>> { it ->
                val recycleList = mutableListOf<RecyclerViewObject>()
                for (i in it) {
                    if (i.currency != BaseCurrencyLiveData.baseCurrency.value) {
                        recycleList.add(i)
                    }
                }
                recyclerList.value = recycleList
            }
        appRepository.recyclerList.observeForever(recyclerViewObserver)
    }

    fun getCachedRates() {
        appRepository.getCachedRates()
    }

    fun checkInternetAvailability(): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val ipProcess = runtime.exec("/system/bin/ping -c 1 " + "www.google.com")
            val exitValue = ipProcess.waitFor()
            exitValue == 0
        } catch (e: Exception) {
            false
        }
    }

    val listOfCurrenciesToConvertFrom = listOf(
        "EUR",
        "CAD",
        "HKD",
        "USD",
        "ISK",
        "PHP",
        "DKK",
        "HUF",
        "KRW",
        "JPY",
        "NZD",
        "RUB",
        "BGN",
        "MXN",
        "THB",
        "IDR",
        "RON",
        "BRL",
        "CNY",
        "NOK",
        "CHF",
        "SEK"
    )

    fun convertCurrencies(currency1: String?, currency2: String?){
        if (currency1 != null && currency2 != null){
            appRepository.getCurrenciesToConvert(currency1, currency2)
        }
    }
    private fun monitorResultsLiveData(){
        appRepository.currencyToConvertList.observeForever {
            if (it.size == 2){
                val result1 = it[0]
                val result2 = it[1]
                resultRate.value = (result1.value / result2.value).toString()
            }else{
                resultRate.value = "1"
            }
        }
    }

}