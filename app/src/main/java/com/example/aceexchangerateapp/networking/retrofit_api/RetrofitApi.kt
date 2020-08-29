package com.example.aceexchangerateapp.networking.retrofit_api

import com.example.aceexchangerateapp.networking.network_dto.ExchangeDto
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.exchangeratesapi.io/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

//app api
interface RetrofitApi {
    @GET("latest")
    fun getAllRates(@Query("base") baseCurrency: String): Deferred<ExchangeDto>
}

object Retrofit{
    val retrofitApi : RetrofitApi by lazy {
        retrofit.create(RetrofitApi::class.java)
    }
}