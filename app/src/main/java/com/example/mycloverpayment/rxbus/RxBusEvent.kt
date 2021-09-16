package com.example.mycloverpayment.rxbus

import com.example.mycloverpayment.model.PaymentOrder

class RxBusEvent{

    data class Details(var invoiceDetail: PaymentOrder, var position: Int)
    data class PaymentId(var invoiceDetail: PaymentOrder, var position: Int)
    data class PaymentStatus(var invoiceDetail: PaymentOrder, var position: Int)
}