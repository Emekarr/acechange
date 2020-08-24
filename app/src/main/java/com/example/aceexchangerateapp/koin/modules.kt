package com.example.aceexchangerateapp.koin

import com.example.aceexchangerateapp.repository.AppRepository
import com.example.aceexchangerateapp.screens.viewmodels.RatesFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { RatesFragmentViewModel(get()) }
}

val modules = module {
    single { AppRepository() }
}