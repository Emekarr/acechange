package com.example.aceexchangerateapp.screens.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aceexchangerateapp.R
import com.example.aceexchangerateapp.databinding.FragmentRatesBinding
import com.example.aceexchangerateapp.screens.recyclerview.CurrencyRecyclerView
import com.example.aceexchangerateapp.screens.viewmodels.RatesFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.koin.android.ext.android.get

class RatesFragment : Fragment() {
    private val viewModel: RatesFragmentViewModel by lazy {
        get<RatesFragmentViewModel>()
    }
    private lateinit var binding: FragmentRatesBinding
    private val isInternetAvailable = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        checkInternetAvailability()
        observeIsInternetAvailable()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rates, container, false)
        binding.ratesFragmentViewModel = viewModel
        binding.lifecycleOwner = this

        setUpRecycler()
        swipeToRefreshList()

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as AppCompatActivity
        mainActivity.setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.helpScreen -> {
                findNavController().navigate(R.id.action_ratesFragment_to_helpFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeIsInternetAvailable() {
        isInternetAvailable.observe(viewLifecycleOwner, Observer {
            if (it) viewModel.getAllRates()
            else viewModel.getCachedRates()
        })
    }

    private fun checkInternetAvailability() {
        val job = Job()
        val ioScope = CoroutineScope(Dispatchers.IO + job)

        ioScope.launch {
            try {
                val runtime = Runtime.getRuntime()
                val ipProcess = runtime.exec("/system/bin/ping -c 1 " + "www.google.com")
                val exitValue = ipProcess.waitFor()
                withContext(Main) {
                    isInternetAvailable.value = exitValue == 0
                }
            } catch (e: Exception) {
                withContext(Main) {
                    isInternetAvailable.value = false
                }
            }
            job.cancel()
        }
    }


    private fun swipeToRefreshList() {
        binding.swipeToRefresh.setOnRefreshListener {
            checkInternetAvailability()
            isInternetAvailable.observe(viewLifecycleOwner, Observer {
                if (it) {
                    viewModel.getAllRates()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Check your internet and try again",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    private fun setUpRecycler() {
        val adapter = CurrencyRecyclerView()
        binding.recyclerView.adapter = adapter
        viewModel.recyclerList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

}