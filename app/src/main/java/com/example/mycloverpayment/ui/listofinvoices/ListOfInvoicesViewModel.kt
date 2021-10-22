package com.example.mycloverpayment.ui.listofinvoices

import android.accounts.Account
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.order.*
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseViewModel
import com.example.mycloverpayment.model.PaymentOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListOfInvoicesViewModel(var context: Context) : BaseViewModel() {

    init {
        toastMessage.value = context.getString(R.string.swipe_option)
    }


}