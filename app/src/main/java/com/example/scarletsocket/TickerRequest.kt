package com.example.scarletsocket

import com.google.gson.annotations.SerializedName

data class TickerRequest(
    val name: String,
    @SerializedName(value = "product_ids")
    val productIds: List<String> = listOf()
)
