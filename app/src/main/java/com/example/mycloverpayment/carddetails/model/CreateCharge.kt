package com.example.mycloverpayment.carddetails.model

data class CreateCharge(
    val amount: Int,
    val currency: String,
    val ecomind: String,
    val source: String
)