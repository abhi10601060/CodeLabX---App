package com.example.codelabx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.codelabx.R

class LoginScreen : Fragment(R.layout.login_screen_layout) {

    private lateinit var loginBtn : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createView(view)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        loginBtn.setOnClickListener(OnClickListener {
            findNavController().navigate(R.id.action_loginScreen_to_signuoScreen)
        })
    }

    private fun createView(view : View) {
        loginBtn = view.findViewById(R.id.login_btn)
    }
}