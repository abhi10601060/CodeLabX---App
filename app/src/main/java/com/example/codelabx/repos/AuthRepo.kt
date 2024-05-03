package com.example.codelabx.repos

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.codelabx.models.AuthResponse
import com.example.codelabx.models.UserDetails
import com.example.codelabx.network.AuthService
import com.example.codelabx.network.Resource
import retrofit2.Response

class AuthRepo(private val authService: AuthService , private val sharedPreferences: SharedPreferences){
    private val authResponseLivedata : MutableLiveData<Resource<AuthResponse>> =   MutableLiveData()
    val authResponse : LiveData<Resource<AuthResponse>>
        get() = authResponseLivedata

    suspend fun signup(userDetails: UserDetails){
        authResponseLivedata.postValue(Resource.Loading<AuthResponse>())
        val response = authService.signUp(userDetails)
        authResponseLivedata.postValue(handleAuthResponseState(response))
    }

    suspend fun login(userDetails: UserDetails){
        authResponseLivedata.postValue(Resource.Loading<AuthResponse>())
        val response = authService.login(userDetails)
        authResponseLivedata.postValue(handleAuthResponseState(response))
    }

    private fun handleAuthResponseState(response: Response<AuthResponse>) : Resource<AuthResponse>{
        if (response.isSuccessful){
                if (response.body()!=null){
                    val token = response.body()!!.token
                    val editor = sharedPreferences.edit()
                    editor.putString("token" , token)
                    editor.apply()
                    return Resource.Success<AuthResponse>(response.body())
                }
        }
        return  Resource.Error<AuthResponse>(response.message())
    }
    fun saveUser(userName : String){
        sharedPreferences.edit().putString("user", userName).apply()
    }
    fun isUserLoggedIn() : Boolean{
        var userName: String? = sharedPreferences.getString("user" , null) ?: return false
        return true
    }
}