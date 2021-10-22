package com.example.mycloverpayment.rxbus

import com.example.mycloverpayment.model.PaymentOrder

class RxBusEvent{

    enum class DialogEventEnum {
         MANUAL, SWIPE
    }

    data class EventDialog(val dialogEvent: DialogEventEnum, val any: Any?)


    data class Result(var id : String, var status : String, var position: Int)
    data class Token(var token : String)


}