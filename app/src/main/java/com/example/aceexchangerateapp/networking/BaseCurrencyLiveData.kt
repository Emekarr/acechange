package com.example.aceexchangerateapp.networking

import androidx.lifecycle.MutableLiveData

object BaseCurrencyLiveData {
    val baseCurrency = MutableLiveData<String>()

    init {
        baseCurrency.value = "EUR"
    }
}