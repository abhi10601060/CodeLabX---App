package com.example.codelabx.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.codelabx.R
import com.example.codelabx.network.AuthService
import com.example.codelabx.network.RetroInstance
import com.example.codelabx.repos.AuthRepo
import com.example.codelabx.utility.SharedPref
import com.example.codelabx.viewmodels.AuthViewModel
import com.example.codelabx.viewmodels.AuthViewModelFactory

class AuthenticationActivity : AppCompatActivity() {

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val authRepo = AuthRepo(RetroInstance.getInstance().create(AuthService::class.java), SharedPref.getAuthDbInstance(this))
        viewModel = ViewModelProvider(this , AuthViewModelFactory(authRepo)).get(AuthViewModel::class.java)
    }
}