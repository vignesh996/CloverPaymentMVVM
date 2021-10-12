package com.example.mycloverpayment.carddetails.model

data class CardTokenErrorResponse(
    val error: Error,
    val message: String
)