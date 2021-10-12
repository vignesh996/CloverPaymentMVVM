package com.example.mycloverpayment.carddetails.model

data class Source(
    val brand: String,
    val cvc_check: String,
    val exp_month: String,
    val exp_year: String,
    val first6: String,
    val id: String,
    val last4: String
)