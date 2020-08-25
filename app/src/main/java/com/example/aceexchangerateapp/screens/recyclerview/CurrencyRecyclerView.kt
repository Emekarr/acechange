package com.example.aceexchangerateapp.screens.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aceexchangerateapp.R
import kotlinx.android.synthetic.main.rates_recycler_card_grid.view.*

class CurrencyRecyclerView: ListAdapter<RecyclerViewObject, CurrencyRecyclerView.ViewHolder>(DiffUtil()){
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        fun bind(currentItem: RecyclerViewObject){
            view.currency.text = currentItem.currency
            view.value.text = currentItem.value.toString()
        }
    }

    class DiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<RecyclerViewObject>(){
        override fun areItemsTheSame(
            oldItem: RecyclerViewObject,
            newItem: RecyclerViewObject
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RecyclerViewObject,
            newItem: RecyclerViewObject
        ): Boolean {
            return oldItem.currency == newItem.currency
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.rates_recycler_card_grid, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}