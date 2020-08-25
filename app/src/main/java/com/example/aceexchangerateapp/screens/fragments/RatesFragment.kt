package com.example.aceexchangerateapp.screens.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aceexchangerateapp.R
import com.example.aceexchangerateapp.databinding.FragmentRatesBinding
import com.example.aceexchangerateapp.screens.recyclerview.CurrencyRecyclerView
import com.example.aceexchangerateapp.screens.viewmodels.RatesFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_rates.*
import org.koin.android.ext.android.get
import java.net.InetSocketAddress
import java.net.Socket

class RatesFragment : Fragment() {
    private val viewModel: RatesFragmentViewModel by lazy {
        get<RatesFragmentViewModel>()
    }
    private lateinit var binding: FragmentRatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (checkInternetAvailability()){
            viewModel.getAllRates()
            Log.i("test", "internet")

        }else{
            viewModel.getCachedRates()
            Log.i("test", "no internet")
        }



        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rates, container, false)



        setUpRecycler()
        swipeToRefreshList()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun checkInternetAvailability(): Boolean {
        return try {
            val timeout = 1500
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(socketAddress, timeout)
            sock.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun swipeToRefreshList() {
        binding.swipeToRefresh.setOnRefreshListener {
            if (checkInternetAvailability()) {
                viewModel.getAllRates()
            }
            else Snackbar.make(requireView(), "Check your internet and try again", Snackbar.LENGTH_SHORT).show()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    private fun setUpRecycler() {
        val adapter = CurrencyRecyclerView()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        viewModel.recyclerList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            for (i in it) {
                Log.i("test", i.currency)
            }
        })
    }

}