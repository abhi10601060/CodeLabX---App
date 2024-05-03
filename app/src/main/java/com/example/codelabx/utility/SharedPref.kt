package com.example.codelabx.utility

import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    fun getAuthDbInstance(context : Context) : SharedPreferences{
        return context.getSharedPreferences("auth_db" , Context.MODE_PRIVATE)
    }
}