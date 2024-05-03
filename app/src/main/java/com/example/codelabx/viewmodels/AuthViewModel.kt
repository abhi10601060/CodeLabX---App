package com.example.codelabx.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.codelabx.models.AuthResponse
import com.example.codelabx.models.UserDetails
import com.example.codelabx.network.Resource
import com.example.codelabx.repos.AuthRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepo: AuthRepo)  : ViewModel(){

    val authResponse : LiveData<Resource<AuthResponse>>
        get() = authRepo.authResponse

    fun signup(userDetails: UserDetails){
        CoroutineScope(Dispatchers.IO).launch { authRepo.signup(userDetails) }
    }

    fun login(userDetails: UserDetails){
        CoroutineScope(Dispatchers.IO).launch { authRepo.login(userDetails) }
    }

    fun saveUser(userName : String){
        authRepo.saveUser(userName)
    }
    fun isUserLoggedIn():Boolean{
        return authRepo.isUserLoggedIn()
    }
}