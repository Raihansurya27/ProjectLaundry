package com.raihan.projectlaundry.model

import java.sql.Timestamp

data class OrderModel(
    val orderId: Int,
    val phoneNumber: String,
    val serviceId: Int,
    val status:String,
    val totalPrice:Int,
    val totalWeight:Int,
    val orderDate:Timestamp,
    val finishDate:Timestamp,
    val discount:Int,
    val afterDiscount:Int)
