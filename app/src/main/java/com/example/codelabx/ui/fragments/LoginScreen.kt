package com.example.codelabx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.codelabx.R
import com.example.codelabx.models.AuthResponse
import com.example.codelabx.models.UserDetails
import com.example.codelabx.network.Resource
import com.example.codelabx.ui.activities.AuthenticationActivity
import com.example.codelabx.ui.activities.MainActivity
import com.example.codelabx.viewmodels.AuthViewModel

class LoginScreen : Fragment(R.layout.login_screen_layout) {

    private lateinit var loginBtn : Button
    private lateinit var signUp : TextView
    private lateinit var  viewModel : AuthViewModel
    private lateinit var emailEdt : EditText
    private lateinit var passEdt : EditText
    private lateinit var progressBar : ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel= (activity as AuthenticationActivity).viewModel

        createView(view)
        setOnClickListeners()
        observeAuthResponse()
    }

    private fun setOnClickListeners() {
        signUp.setOnClickListener(OnClickListener {
            findNavController().navigate(R.id.action_loginScreen_to_signuoScreen)
        })

        loginBtn.setOnClickListener(OnClickListener {
            if(!validUserNameAndPass()){
                emailEdt.setError("Empty Credentials, Please recheck.")
                return@OnClickListener
            }
            val userDetails = UserDetails(emailEdt.text.toString() , passEdt.text.toString())
            viewModel.login(userDetails)
        })
    }

    private fun validUserNameAndPass() : Boolean{
        val enteredEmail = emailEdt.text.toString().trim()
        val enteredPass = passEdt.text.toString().trim()

        if (enteredPass.equals("") or enteredEmail.equals("")){
            return false
        }
        return true
    }

    private fun observeAuthResponse(){
        viewModel.authResponse.observe(this.viewLifecycleOwner , Observer{
            when(it){

                is Resource.Loading<AuthResponse> ->{
                    progressBar.visibility = View.VISIBLE
                    loginBtn.isClickable = false
                }

                is Resource.Error<AuthResponse> -> {
                    progressBar.visibility = View.GONE
                    loginBtn.isClickable = true
                    emailEdt.setError("Wrong Credentials, Please recheck.")
                }

                is Resource.Success<AuthResponse> ->{
                    progressBar.visibility = View.VISIBLE
                    loginBtn.isClickable = true
                    openMainActivity()
                }
            }
        })
    }

    private fun openMainActivity() {
        val intent = Intent(activity , MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun createView(view : View) {
        loginBtn = view.findViewById(R.id.login_btn)
        signUp = view.findViewById(R.id.login_signup_text)
        emailEdt = view.findViewById(R.id.login_email_edt)
        passEdt = view.findViewById(R.id.login_pass_edt)
        progressBar = view.findViewById(R.id.login_progress_bar)
    }
}