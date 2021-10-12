package com.example.mycloverpayment.listofinvoices

import android.accounts.Account
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.order.*
import com.example.mycloverpayment.model.PaymentOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListOfInvoicesViewModel : ViewModel() {

    private var mAccount: Account? = null
    private var mInventoryConnector: InventoryConnector? = null
    private var orderConnector: OrderConnector? = null
    lateinit var invoiceDetail: PaymentOrder
    var invoicePosition: Int = 0
    lateinit var mOrder: Order
    lateinit var invoicesList: ArrayList<PaymentOrder>

    private var callBackInterface: CallBackInterfaceForLauncher? = null

    fun setCallBackInterface(callBackInterface: CallBackInterfaceForLauncher) {
        this.callBackInterface = callBackInterface
    }

    suspend fun lunchBackgroundThread(
        invoiceDetails: PaymentOrder,
        position: Int,
        account: Account?,
        inventoryConnector: InventoryConnector?,
        orderConnector: OrderConnector?
    ): PaymentOrder {
        this.mAccount = account
        this.mInventoryConnector = inventoryConnector
        this.orderConnector = orderConnector

        return withContext(Dispatchers.IO) {
            invoiceDetail = invoiceDetails
            invoicePosition = position
            var details = createOrder(invoiceDetails, position)
            return@withContext details
        }
    }

    private fun createOrder(details: PaymentOrder, position: Int): PaymentOrder {

        if (details.cloverOrderId.isNullOrEmpty()) {
            mOrder = orderConnector!!.createOrder(Order())
            mOrder.setPayType(PayType.FULL)
            mOrder.setState("open")
            details.uniqueId = mOrder.id
            details.cloverOrderId = mOrder.id
            setCloverOrderId(details, position)
            addCustomLineItem(details)
        } else {
            mOrder = orderConnector!!.getOrder(details.uniqueId)
        }

        return details
    }

    private fun setCloverOrderId(details: PaymentOrder, position: Int) {
        invoicesList[position].cloverOrderId = details.cloverOrderId
    }

    private fun addCustomLineItem(details: PaymentOrder) {
        var customLineItem = LineItem().apply {
            price = details.amount
            note = "Payment Made by Vignesh"
            this.name = details.customerName
        }
        orderConnector!!.addCustomLineItem(details.uniqueId, customLineItem, false)
    }


    fun getInvoiceList(): ArrayList<PaymentOrder> {
        invoicesList = ArrayList()

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

    interface CallBackInterfaceForLauncher {
        fun resultLauncher(intent: Intent)
    }

    fun createAuthorizationOfPayment(authToken :String,mId: String) = liveData(Dispatchers.IO) {
        emit(ListOfInvoicesRepository().createAuthorizationOfPayment(authToken, mId))
    }
}