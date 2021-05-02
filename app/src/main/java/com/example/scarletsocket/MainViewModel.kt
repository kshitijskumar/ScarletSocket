package com.example.scarletsocket

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {


    private val socketScope = viewModelScope + CoroutineScope(Dispatchers.IO).coroutineContext

    private val socket by lazy {
        SocketService(application)
    }
    private val repository by lazy {
        MainRepository(socket)
    }

    var socketEvent : Flow<WebSocket.Event> = flowOf()
        private set

//    private val _ticker = MutableLiveData<TickerResponse>()
//    val ticker: LiveData<TickerResponse>
//        get() = _ticker
    var ticker : Flow<TickerResponse> = repository.observeTicker().consumeAsFlow()


    init {
        viewModelScope.launch {
            socketEvent = repository.observeWebSocketEvent().consumeAsFlow()
        }

        observeTickers()
    }

    fun sendSubscribeAction(action: SubscribeAction) = viewModelScope.launch {
        repository.subscribe(action)
    }

    fun observeTickers() = viewModelScope.launch {
        val tick = repository.observeTicker().consumeEach {
            Log.d("MainViewModel", "Resp is: $it")
        }
//        _ticker.postValue(repository.observeTicker().consumeAsFlow().asLiveData(viewModelScope.coroutineContext).value)
    }

}