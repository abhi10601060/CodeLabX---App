package com.example.codelabx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.codelabx.R
import com.example.codelabx.ui.activities.MainActivity

class SignuoScreen : Fragment(R.layout.signup_screen_layout) {

    private lateinit var signUpBtn : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createView(view)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        signUpBtn.setOnClickListener(OnClickListener {
            val intent = Intent(activity , MainActivity::class.java)
            startActivity(intent)
        })
    }

    private fun createView(view: View) {
        signUpBtn = view.findViewById(R.id.signup_btn)
    }
}