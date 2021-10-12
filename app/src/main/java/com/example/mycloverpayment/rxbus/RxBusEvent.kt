package com.example.mycloverpayment.rxbus

import com.example.mycloverpayment.model.PaymentOrder

class RxBusEvent{

    data class Result(var id : String, var status : String, var position: Int)
    data class Token(var token : String)
    data class PaymentId(var invoiceDetail: PaymentOrder, var position: Int)
    data class PaymentStatus(var invoiceDetail: PaymentOrder, var position: Int)

}