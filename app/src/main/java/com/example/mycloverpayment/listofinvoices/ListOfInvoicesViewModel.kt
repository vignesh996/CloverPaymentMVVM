package com.example.mycloverpayment.listofinvoices

import android.accounts.Account
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.order.*
import com.example.mycloverpayment.base.BaseViewModel
import com.example.mycloverpayment.model.PaymentOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListOfInvoicesViewModel(var context: Context) :BaseViewModel() {

    private var mAccount: Account? = null
    var mInventoryConnector: InventoryConnector? = null
    var orderConnector: OrderConnector? = null

    fun getCloverAccount(): Account? {
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(context)
            if (mAccount == null) {
                return null
            }
        }
        return mAccount
    }

    fun connect() {
        disconnect()
        if (mAccount != null) {
            mInventoryConnector = InventoryConnector(context, mAccount, null)
            mInventoryConnector!!.connect()
            orderConnector = OrderConnector(context, mAccount, null)
            orderConnector!!.connect()
        }
    }

     fun disconnect() {
        if (mInventoryConnector != null) {
            mInventoryConnector!!.disconnect()
            mInventoryConnector = null
        }

        if (orderConnector != null) {
            orderConnector!!.disconnect()
            orderConnector = null
        }
    }






}