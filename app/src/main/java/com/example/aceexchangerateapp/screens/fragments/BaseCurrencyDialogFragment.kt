package com.example.aceexchangerateapp.screens.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.aceexchangerateapp.R
import com.example.aceexchangerateapp.databinding.BaseCurrencyDialogFragmentBinding
import com.example.aceexchangerateapp.networking.BaseCurrencyLiveData

class BaseCurrencyDialogFragment: DialogFragment() {
    private lateinit var binding: BaseCurrencyDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.base_currency_dialog_fragment, container, false)

        val baseCurrencyList = arrayOf("EUR", "CAD", "HKD", "USD", "KRW", "JPY", "NZD")

        binding.selectBaseCurrency.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, baseCurrencyList)

        binding.selectBaseCurrency.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                BaseCurrencyLiveData.baseCurrency.value = baseCurrencyList[position]
                this.dismiss()
            }

        return binding.root
    }
}