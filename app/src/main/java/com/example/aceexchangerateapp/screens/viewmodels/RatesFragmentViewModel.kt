package com.example.aceexchangerateapp.screens.viewmodels

import androidx.lifecycle.ViewModel
import com.example.aceexchangerateapp.repository.AppRepository

class RatesFragmentViewModel(private val appRepository: AppRepository): ViewModel() {
    fun getAllRates(){
        appRepository.getAllRates()
    }


}