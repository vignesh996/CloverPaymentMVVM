package com.example.mycloverpayment.carddetails.model

data class Card(
    val brand: String,
    val exp_month: String,
    val exp_year: String,
    val first6: String,
    val last4: String
)