package com.example.aceexchangerateapp.screens.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.aceexchangerateapp.R
import com.example.aceexchangerateapp.databinding.FragmentRatesBinding
import com.example.aceexchangerateapp.networking.BaseCurrencyLiveData
import com.example.aceexchangerateapp.screens.recyclerview.CurrencyRecyclerView
import com.example.aceexchangerateapp.screens.viewmodels.RatesFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.converter_bottomsheet.*
import kotlinx.android.synthetic.main.converter_bottomsheet.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.koin.android.ext.android.get
import java.io.IOException

class RatesFragment : Fragment() {
    private val viewModel: RatesFragmentViewModel by lazy {
        get<RatesFragmentViewModel>()
    }
    private val baseCurrencyDialogFragment: BaseCurrencyDialogFragment by lazy {
        BaseCurrencyDialogFragment()
    }
    private val bottomSheetBehavior: BottomSheetBehavior<MaterialCardView> by lazy {
        BottomSheetBehavior.from(converterBottomSheet)
    }
    private val convertFromLiveData = MutableLiveData<String>()
    private val convertToLiveData = MutableLiveData<String>()

    private lateinit var binding: FragmentRatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.checkInternetAvailability()
        observeIsInternetAvailable()
        onChangeOfBaseCurrency()
        observeConvertCurrenciesLiveData()
        monitorResultRates()


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rates, container, false)
        binding.baseCurrencyLiveData = BaseCurrencyLiveData
        binding.lifecycleOwner = this

        setUpRecycler()
        swipeToRefreshList()

        //click listeners
        binding.baseCurrency.setOnClickListener {
            baseCurrencyDialogFragment.show(requireActivity().supportFragmentManager, "")
        }


        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUpBottomSheet() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheet.convertersheetHeader.text =
                        "Convert your currencies"
                    BottomSheetBehavior.STATE_COLLAPSED -> bottomSheet.convertersheetHeader.text =
                        "Slide up to convert"
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomSheet()
        val mainActivity = requireActivity() as AppCompatActivity
        mainActivity.setSupportActionBar(binding.toolbar)
        setUpConvertFromCurrencySpinner()
        setUpConvertToCurrencySpinner()
        convertButton.setOnClickListener {
            convertCurrencies(convertFromLiveData.value, convertToLiveData.value)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.helpScreen -> {
                findNavController().navigate(R.id.action_ratesFragment_to_helpFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onChangeOfBaseCurrency() {
        BaseCurrencyLiveData.baseCurrency.observe(viewLifecycleOwner, Observer { currency ->
            val job = Job()
            val ioScope = CoroutineScope((Dispatchers.IO + job))
            ioScope.launch {
                if (viewModel.checkInternetAvailability()) viewModel.getAllRates(currency)
                 else {
                    Snackbar.make(
                        requireView(),
                        "Check your internet and try again",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                job.cancel()
            }
        })
    }

    private fun observeIsInternetAvailable() {
        val job = Job()
        val ioScope = CoroutineScope((Dispatchers.IO + job))
        ioScope.launch {
            if (viewModel.checkInternetAvailability()) viewModel.getAllRates(BaseCurrencyLiveData.baseCurrency.value!!)
            else withContext(Main){
                viewModel.getCachedRates()
            }
            job.cancel()
        }
    }


    private fun swipeToRefreshList() {
        binding.swipeToRefresh.setOnRefreshListener {
            val job = Job()
            val ioScope = CoroutineScope((Dispatchers.IO + job))
            ioScope.launch {
                if (viewModel.checkInternetAvailability()) viewModel.getAllRates(BaseCurrencyLiveData.baseCurrency.value!!)
                else Snackbar.make(
                    requireView(),
                    "Check your internet and try again",
                    Snackbar.LENGTH_SHORT
                ).show()
                job.cancel()
            }
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

    private fun setUpConvertFromCurrencySpinner(){
        val currencyList = viewModel.listOfCurrenciesToConvertFrom
        converterBottomSheet.convertFromSpinner.adapter =   ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, currencyList)
        converterBottomSheet.convertFromSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertFromLiveData.value =  currencyList[position]
            }
        }
    }

    private fun setUpConvertToCurrencySpinner(){
        val currencyList = viewModel.listOfCurrenciesToConvertFrom
        converterBottomSheet.convertToSpinner.adapter =   ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, currencyList)
        converterBottomSheet.convertToSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertToLiveData.value =  currencyList[position]
            }
        }
    }

    private fun observeConvertCurrenciesLiveData(){
        convertFromLiveData.observe(viewLifecycleOwner, Observer {
            convertFrom.text = "convert from " + it
        })

        convertToLiveData.observe(viewLifecycleOwner, Observer {
            convertTo.text = "convert to " + it
        })
    }

    private fun convertCurrencies(currency1: String?, currency2: String?){
        viewModel.convertCurrencies(currency1, currency2)
    }
    private  fun monitorResultRates(){
        viewModel.resultRate.observe(viewLifecycleOwner, Observer {
            converterBottomSheet.convertedResult.text = it
        })
    }

}