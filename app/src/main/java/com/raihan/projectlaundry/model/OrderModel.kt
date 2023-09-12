package com.raihan.projectlaundry.model

import java.sql.Timestamp

data class OrderModel(
    val orderId: String,
    val phoneNumber: String,
    val serviceId: String,
    val status: String,
    val totalPrice: Int,
    val totalWeight: Float,
    val orderDate: Timestamp,
    val finishDate: Timestamp,
    val discount: Int,
    val afterDiscount: Int,
    val pickUp:String,
    val dropOff:String
)
