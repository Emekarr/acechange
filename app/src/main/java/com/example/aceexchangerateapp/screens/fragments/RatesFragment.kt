package com.example.aceexchangerateapp.screens.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aceexchangerateapp.R
import com.example.aceexchangerateapp.screens.viewmodels.RatesFragmentViewModel
import org.koin.android.ext.android.get

class RatesFragment : Fragment() {
    private val viewModel: RatesFragmentViewModel by lazy {
        get<RatesFragmentViewModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getAllRates()


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rates, container, false)
    }

}