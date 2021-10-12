package com.example.mycloverpayment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceDetail(
    var invoiceNo : String ,var uniqueId: String, var customerName: String,
    var customerId: String, var amount: Long, var paymentStatus: String, var cloverOrderId : String, var position :Int
): Parcelable
