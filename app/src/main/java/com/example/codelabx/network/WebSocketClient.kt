package com.example.codelabx.network

import android.content.Context
import com.example.codelabx.utility.SharedPref
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketClient {

    companion object{
        private val url = "ws://ec2-13-232-1-253.ap-south-1.compute.amazonaws.com:8010/handShake"
        private val okhttpClient = OkHttpClient()

        fun getWebSocketConn(webSocketListener: WebSocketListener, context: Context) : WebSocket{
            val db = SharedPref.getAuthDbInstance(context)
            val token = db.getString(SharedPref.TOKEN_KEY, "")
            val user = db.getString(SharedPref.USER_KEY, "")
            return  okhttpClient.newWebSocket(createRequest(token!! , user!!) , webSocketListener)
        }

        private fun createRequest(token : String, user : String) : Request{
            return  Request.Builder()
                .addHeader("token", token)
                .addHeader("userName" , user)
                .url(url)
                .build()
        }
    }
}