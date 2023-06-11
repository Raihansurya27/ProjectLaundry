package com.raihan.projectlaundry.model

import com.google.gson.annotations.SerializedName

data class OrderDetailModel(
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("quantity")
    val quantity: Int,
)
