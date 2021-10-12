package com.example.mycloverpayment.model

data class PaymentOrder(
    var invoiceNo : String ,var uniqueId: String, var customerName: String,
    var customerId: String, var amount: Long, var paymentStatus: String, var cloverOrderId : String
)
