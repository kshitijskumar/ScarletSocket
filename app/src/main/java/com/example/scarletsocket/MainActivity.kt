package com.example.scarletsocket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.scarletsocket.databinding.ActivityMainBinding
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MainViewModel::class.java)

        lifecycleScope.launch {
            viewModel.socketEvent.collect {
                when(it) {
                    is WebSocket.Event.OnConnectionOpened<*> -> {
                        Log.d("MainActivity", "Connected is ${it.webSocket}")

                        val productIds = listOf("ETH-BTC")
                        val tickerRequests = listOf(TickerRequest(productIds = productIds, name = "ticker"))
                        val bitcoinSubscribeAction = SubscribeAction(productIds = productIds, channels = tickerRequests)

                        viewModel.sendSubscribeAction(bitcoinSubscribeAction)
                    }
                    is WebSocket.Event.OnConnectionFailed -> {
                        Log.d("MainActivity", "Failed is ${it.throwable.localizedMessage}")
                    }
                    is WebSocket.Event.OnConnectionClosed -> {
                        Log.d("MainActivity", "Closed is ${it.shutdownReason.reason}")
                    }
                    is WebSocket.Event.OnConnectionClosing -> {
                        Log.d("MainActivity", "Closing is ${it.shutdownReason.reason}")
                    }
                    is WebSocket.Event.OnMessageReceived -> {
                        Log.d("MainActivity", "Received is ${it.message}")
                    }
                }
            }
        }

//        viewModel.observeTickers()

        lifecycleScope.launch {
            viewModel.ticker.collect {
                Log.d("FlowResp", "resp is $it and ${it.hashCode()}")
                counter++
                if(counter == 10) {
                    val productIds = listOf("ETH-BTC")
                    val tickerRequests = listOf(TickerRequest(productIds = productIds, name = "ticker"))
                    val bitcoinSubscribeAction = SubscribeAction(productIds = productIds, channels = tickerRequests, type = "unsubscribe")

                    viewModel.sendSubscribeAction(bitcoinSubscribeAction)
                }

            }
        }
//        viewModel.ticker.observe(this) {
//            Log.d("TickerResp", "resp is $it")
//        }
    }
}