package com.example.codelabx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.codelabx.repos.AuthRepo

class AuthViewModelFactory(private val authRepo: AuthRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AuthViewModel(authRepo) as T
    }
}