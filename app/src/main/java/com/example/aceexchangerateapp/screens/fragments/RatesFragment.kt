package com.example.aceexchangerateapp.screens.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
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
    private lateinit var binding: FragmentRatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.checkInternetAvailability()
        observeIsInternetAvailable()
        onChangeOfBaseCurrency()


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rates, container, false)
        binding.ratesFragmentViewModel = viewModel
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
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheet.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                        bottomSheet.convertersheetHeader.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheet.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.white
                            )
                        )
                        bottomSheet.convertersheetHeader.setTextColor(
                            Color.parseColor("#121212")
                        )
                    }
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
            if (viewModel.checkInternetAvailability()) {
                viewModel.getAllRates(currency)
            } else {
                Snackbar.make(
                    requireView(),
                    "Check your internet and try again",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun observeIsInternetAvailable() {
        if (viewModel.checkInternetAvailability()) viewModel.getAllRates(BaseCurrencyLiveData.baseCurrency.value!!)
        else viewModel.getCachedRates()
    }


    private fun swipeToRefreshList() {
        binding.swipeToRefresh.setOnRefreshListener {
            if (viewModel.checkInternetAvailability()) viewModel.getAllRates(BaseCurrencyLiveData.baseCurrency.value!!)
            else Snackbar.make(
                requireView(),
                "Check your internet and try again",
                Snackbar.LENGTH_SHORT
            ).show()
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