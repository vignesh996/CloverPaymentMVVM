package com.example.mycloverpayment.helper

import com.example.mycloverpayment.model.PaymentOrder

class StaticInvoiceList {

     fun getInvoiceList(): ArrayList<PaymentOrder> {

        var invoicesList = ArrayList<PaymentOrder>()

        invoicesList.add(PaymentOrder("454","", "Vignesh", "78945", 500L, "open", ""))
        invoicesList.add(PaymentOrder("484","", "Arun", "78946", 700L, "open", ""))
        invoicesList.add(PaymentOrder("404","", "Suresh", "78947", 900L, "open", ""))
        invoicesList.add(PaymentOrder("4564","", "Jaya", "78948", 1500L, "open", ""))
        invoicesList.add(PaymentOrder("444","", "Kamal", "78949", 5000L, "open", ""))
        invoicesList.add(PaymentOrder("434","", "Partha", "78910", 2500L, "open", ""))
        invoicesList.add(PaymentOrder("424","", "Murty", "78911", 7500L, "open", ""))
        invoicesList.add(PaymentOrder("414","", "Vijay", "78912", 9500L, "open", ""))
        invoicesList.add(PaymentOrder("454","", "Magesh", "78913", 6500L, "open", ""))
        invoicesList.add(PaymentOrder("4534","", "Muthu", "78914", 700L, "open", ""))
        invoicesList.add(PaymentOrder("494","", "Surya", "78915", 500L, "open", ""))
        invoicesList.add(PaymentOrder("484","", "Latchu", "78916", 800L, "open", ""))
        invoicesList.add(PaymentOrder("4364","", "Mugu", "78917", 200L, "open", ""))

        return invoicesList
    }
}