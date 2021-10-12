package com.example.mycloverpayment.carddetails.model

data class RetriveKeyResponse(
    val active: Boolean,
    val apiAccessKey: String,
    val createdTime: Long,
    val developerAppUuid: String,
    val merchantUuid: String,
    val modifiedTime: Long
)