package com.example.scarletsocket

import com.google.gson.annotations.SerializedName


data class SubscribeAction(
    val type: String = "subscribe",
    @SerializedName(value = "product_ids")
    val productIds: List<String> = listOf(),
    val channels: List<TickerRequest>
)
