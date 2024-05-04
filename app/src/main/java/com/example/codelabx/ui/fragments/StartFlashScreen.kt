package com.example.codelabx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.codelabx.R
import com.example.codelabx.ui.activities.AuthenticationActivity
import com.example.codelabx.ui.activities.MainActivity
import com.example.codelabx.viewmodels.AuthViewModel

class StartFlashScreen : Fragment() {

    private lateinit var viewModel : AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.start_flash_screen_layout , container , false)

        viewModel = (activity as AuthenticationActivity).viewModel

        Handler().postDelayed(Runnable {
            if (viewModel.isUserLoggedIn()) openMainActivity()
            else findNavController().navigate(R.id.action_startFlashScreen_to_loginScreen)
        } , 1000)

        return view
    }

    private fun openMainActivity() {
        val intent = Intent(activity , MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}