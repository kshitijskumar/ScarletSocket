package com.example.scarletsocket

import android.util.Log
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.withContext

class MainRepository(
    private val socket: SocketService
) {

    fun observeWebSocketEvent() : ReceiveChannel<WebSocket.Event>{
        return socket.observeWebSocketEvent()
    }

    suspend fun subscribe(action: SubscribeAction) {
        return withContext(Dispatchers.IO) {
            socket.subscribe(action)
        }
    }

    fun observeTicker() : ReceiveChannel<TickerResponse> {
        val resp = socket.observeTicker()
        Log.d("MainRepo", "resp is $resp")
        return resp
    }
}