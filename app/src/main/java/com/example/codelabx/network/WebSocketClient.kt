package com.example.codelabx.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketClient {

    companion object{
        private val url = "wss://socketsbay.com/wss/v2/1/demo/"
        private val okhttpClient = OkHttpClient()

        fun getWebSocketConn(webSocketListener: WebSocketListener) : WebSocket{
            return  okhttpClient.newWebSocket(createRequest() , webSocketListener)
        }

        private fun createRequest() : Request{
            return  Request.Builder()
                .url(url)
                .build()
        }
    }
}