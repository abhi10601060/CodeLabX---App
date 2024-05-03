package com.example.codelabx.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.codelabx.R
import com.example.codelabx.models.AuthResponse
import com.example.codelabx.models.UserDetails
import com.example.codelabx.network.Resource
import com.example.codelabx.ui.activities.AuthenticationActivity
import com.example.codelabx.ui.activities.MainActivity
import com.example.codelabx.viewmodels.AuthViewModel

class SignupScreen : Fragment(R.layout.signup_screen_layout) {

    private lateinit var signUpBtn : Button
    private lateinit var emailEdt : EditText
    private lateinit var passEdt : EditText
    private lateinit var confirmPassEdt : EditText
    private lateinit var viewModel: AuthViewModel
    private lateinit var progressBar : ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as AuthenticationActivity).viewModel

        createView(view)
        setOnClickListeners()
        observeAuthResponse()
    }

    private fun setOnClickListeners() {
        signUpBtn.setOnClickListener(OnClickListener {
            val res = validUserNameAndPass();
            if (res == 1){
                emailEdt.setError("Empty Credentials, Please recheck.")
                return@OnClickListener
            }
            if (res == 2){
                confirmPassEdt.setError("Confirm password doesn't match")
                return@OnClickListener
            }
            val userDetails = UserDetails(emailEdt.text.toString().trim() , confirmPassEdt.text.toString().trim())
            viewModel.signup(userDetails)
        })
    }

    private fun validUserNameAndPass() : Int{
        val enteredEmail = emailEdt.text.toString().trim()
        val enteredPass = passEdt.text.toString().trim()
        val enteredConfirmPass = confirmPassEdt.text.toString().trim()

        if (enteredPass.equals("") or enteredEmail.equals("") or enteredConfirmPass.equals("")) return 1
        if(!enteredPass.equals(enteredConfirmPass)) return 2
        return 0
    }

    private fun observeAuthResponse(){
        viewModel.authResponse.observe(this.viewLifecycleOwner , Observer{
            when(it){

                is Resource.Loading<AuthResponse> ->{
                    progressBar.visibility = View.VISIBLE
                    signUpBtn.isClickable = false
                }

                is Resource.Error<AuthResponse> -> {
                    progressBar.visibility = View.GONE
                    signUpBtn.isClickable = true
                    emailEdt.setError("Wrong Credentials, Please recheck.")
                }

                is Resource.Success<AuthResponse> ->{
                    progressBar.visibility = View.VISIBLE
                    signUpBtn.isClickable = true
                    saveUser()
                    openMainActivity()
                }
            }
        })
    }
    private fun saveUser() {
        viewModel.saveUser(emailEdt.text.toString().trim())
    }
    private fun openMainActivity() {
        val intent = Intent(activity , MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun createView(view: View) {
        signUpBtn = view.findViewById(R.id.signup_btn)
        emailEdt = view.findViewById(R.id.signup_email_edt)
        passEdt = view.findViewById(R.id.signup_pass_edt)
        confirmPassEdt = view.findViewById(R.id.signup_confirm_pass_edt)
        progressBar = view.findViewById(R.id.signup_progress_bar)
    }
}