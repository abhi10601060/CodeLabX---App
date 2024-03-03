package com.example.codelabx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.codelabx.repos.MainRepo

class MainViewModelFactory(private val repo : MainRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(MainRepo()) as T
    }
}