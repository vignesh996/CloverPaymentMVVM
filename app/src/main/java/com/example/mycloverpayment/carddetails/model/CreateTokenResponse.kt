package com.example.mycloverpayment.carddetails.model

import com.google.gson.annotations.SerializedName

data class CreateTokenResponse(
    val card: Card,
    val id: String,
    val `object`: String
)