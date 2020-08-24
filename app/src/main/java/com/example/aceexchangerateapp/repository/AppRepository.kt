package com.example.aceexchangerateapp.repository

import android.util.Log
import com.example.aceexchangerateapp.networking.retrofit_api.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//repository
class AppRepository {
    private val retrofitApi = Retrofit.retrofitApi

    fun getAllRates(){
        val job = Job()
        val ioScope = CoroutineScope(Dispatchers.IO + job)
        ioScope.launch {
            val exchangeDto = retrofitApi.getAllRates().await()
            Log.i("test", exchangeDto.rates.BGN.toString())
            job.cancel()
        }
    }

}