package com.example.aceexchangerateapp.screens.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.aceexchangerateapp.repository.AppRepository
import com.example.aceexchangerateapp.screens.recyclerview.RecyclerViewObject

class RatesFragmentViewModel(private val appRepository: AppRepository) : ViewModel() {

    val recyclerList = MutableLiveData<List<RecyclerViewObject>>()
    val baseCurrency = MutableLiveData<String>()

    init {
        getAndObserveRecyclerListAndBaseCurrencyFromRepo()
    }

    fun getAllRates() {
        appRepository.getAllRates()
    }

    private fun getAndObserveRecyclerListAndBaseCurrencyFromRepo() {
        val recyclerViewObserver = Observer<List<RecyclerViewObject>> {
            recyclerList.value = it
        }
        appRepository.recyclerList.observeForever(recyclerViewObserver)

        val baseCurrencyObserver = Observer<String> {
            baseCurrency.value = it
            Log.i("test", "base currency vm" + it )
        }
        appRepository.baseCurrency.observeForever(baseCurrencyObserver)
    }

    fun getCachedRates() {
        appRepository.getCachedRates()
    }

}