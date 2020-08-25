package com.example.aceexchangerateapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aceexchangerateapp.screens.recyclerview.RecyclerViewObject

@Entity(tableName = "currency_entity")
data class CurrencyEntity(
    @PrimaryKey
    var currency: String,
    var value: Float
)

fun List<CurrencyEntity>.transformToRecyclerViewObject(): List<RecyclerViewObject> {
    return this.map {
        RecyclerViewObject(
            currency = it.currency,
            value = it.value
        )
    }
}