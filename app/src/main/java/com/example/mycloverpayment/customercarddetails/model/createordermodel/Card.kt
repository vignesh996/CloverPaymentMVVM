package com.example.mycloverpayment.customercarddetails.model.createordermodel

data class Card(
    val expirationDate: String,
    val first6: String,
    val firstName: String,
    val last4: String,
    val lastName: String,
    val cardType: String
)