package com.example.mycloverpayment.customercarddetails.model.createordermodel

data class CreateCustomerResponse(
    val customerSince: Long,
    val firstName: String,
    val href: String,
    val id: String,
    val lastName: String,
    val marketingAllowed: Boolean
)