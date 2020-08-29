package com.example.aceexchangerateapp.networking.network_dto

data class ExchangeDto(
    val base: String,
    val date: String,
    val rates: ExchangeRates)

data class ExchangeRates(
    val EUR: Float?,
    val CAD: Float?,
    val HKD: Float?,
    val USD: Float?,
    val ISK: Float,
    val PHP: Float,
    val DKK: Float,
    val HUF: Float,
    val KRW: Float?,
    val JPY: Float?,
    val NZD: Float?,
    val RUB: Float,
    val BGN: Float,
    val MXN: Float,
    val THB: Float,
    val IDR: Float,
    val RON: Float,
    val BRL: Float,
    val CNY: Float,
    val NOK: Float,
    val CHF: Float,
    val SEK: Float
)
