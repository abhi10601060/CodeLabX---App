package com.example.codelabx.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.codelabx.R

class StartFlashScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.start_flash_screen_layout , container , false)

        Handler().postDelayed(Runnable {
            findNavController().navigate(R.id.action_startFlashScreen_to_loginScreen)
        } , 1000)

        return view
    }
}