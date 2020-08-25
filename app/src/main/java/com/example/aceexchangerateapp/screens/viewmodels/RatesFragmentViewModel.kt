package com.example.aceexchangerateapp.screens.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.aceexchangerateapp.database.CurrencyEntity
import com.example.aceexchangerateapp.repository.AppRepository
import com.example.aceexchangerateapp.screens.recyclerview.RecyclerViewObject

class RatesFragmentViewModel(private val appRepository: AppRepository): ViewModel() {

    val recyclerList = MutableLiveData<List<RecyclerViewObject>>()

    init {
        getAndObserveRecyclerListFromRepo()
    }

    fun getAllRates(){
        appRepository.getAllRates()
    }

    private fun getAndObserveRecyclerListFromRepo(){
        val observer = Observer<List<RecyclerViewObject>>{
            recyclerList.value = it
        }
        appRepository.recyclerList.observeForever(observer)
    }

    fun getCachedRates(){
        appRepository.getCachedRates()
    }


}