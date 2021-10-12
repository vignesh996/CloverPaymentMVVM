package com.example.mycloverpayment.carddetails.model

data class CreateChargeResponse(
    val amount: Int,
    val amount_refunded: Int,
    val auth_code: String,
    val captured: Boolean,
    val created: Long,
    val currency: String,
    val ecomind: String,
    val id: String,
    val outcome: Outcome,
    val paid: Boolean,
    val ref_num: String,
    val source: Source,
    val status: String
)