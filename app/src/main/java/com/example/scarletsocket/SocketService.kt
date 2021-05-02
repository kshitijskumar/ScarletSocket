package com.example.scarletsocket

import android.app.Application
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import kotlinx.coroutines.channels.ReceiveChannel
import okhttp3.OkHttpClient

interface SocketService {

    @Send
    fun subscribe(action : SubscribeAction)

    @Receive
    fun observeTicker() : ReceiveChannel<TickerResponse>

    @Receive
    fun observeWebSocketEvent() : ReceiveChannel<WebSocket.Event>

    companion object {
        operator fun invoke(application: Application) : SocketService {
            val lifecycle = AndroidLifecycle.ofApplicationForeground(application)
            val backoffStrategy = ExponentialBackoffStrategy(5000, 5000)
            val okHttpClient = OkHttpClient.Builder().build()

            return Scarlet.Builder()
                .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
                .addMessageAdapterFactory(GsonMessageAdapter.Factory())
                .backoffStrategy(backoffStrategy)
                .lifecycle(lifecycle)
                .webSocketFactory(okHttpClient.newWebSocketFactory("wss://ws-feed.pro.coinbase.com"))
                .build()
                .create(SocketService::class.java)
        }
    }
}