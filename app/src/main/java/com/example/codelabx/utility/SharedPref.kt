package com.example.codelabx.utility

import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    val USER_KEY = "user"
    val TOKEN_KEY = "token"
     fun getAuthDbInstance(context : Context) : SharedPreferences{
        return context.getSharedPreferences("auth_db" , Context.MODE_PRIVATE)
    }
}