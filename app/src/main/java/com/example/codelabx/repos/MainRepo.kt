package com.example.codelabx.repos

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.codelabx.models.UserEvent
import com.example.codelabx.network.WebSocketClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

object MainRepo  : WebSocketListener() {

     private var webSocket: WebSocket? = null

     private var stdoutLiveData = MutableLiveData<String>()
     val stdout : LiveData<String>
        get() = stdoutLiveData

    private var connFailureLivedata = MutableLiveData<Int>()
    val connFailure : LiveData<Int>
        get() = connFailureLivedata

    fun setWebSocketConn(context : Context){
        if (webSocket != null){
            closeWebsocketConn()
            Log.d("ABHI", "setWebSocketConn: closed prev web socket")
        }
        webSocket = WebSocketClient.getWebSocketConn(this , context)
    }

    fun closeWebsocketConn(){
        if (webSocket == null) return
        webSocket!!.close(1000 , "Closed Manually")
        webSocket = null
    }

    fun writeMessageToConn(userEvent: UserEvent, context : Context){
        val gson = Gson()
        val res = webSocket?.send(gson.toJson(userEvent))
        if (res==null || !res) {
            setWebSocketConn(context)
            writeMessageToConn(userEvent , context)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("ABHI", "onClosed with code : $code and message : $reason")
        closeWebsocketConn()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d("ABHI", "onFailure: called with code ${response?.code} and message : ${response?.message}")
        if (response?.message.equals("Unauthorized" , true)){
            connFailureLivedata.postValue(0)
        }
        else{
            connFailureLivedata.postValue(response?.code)
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d("ABHI", "onMessage: $text")
        this.stdoutLiveData.postValue(text)
        Log.d("WEBSOCKET", "text in live: " + this.stdoutLiveData.value)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d("ABHI", "onMessage: $bytes")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d("ABHI", "onOpen: ${response.message}")
    }
}