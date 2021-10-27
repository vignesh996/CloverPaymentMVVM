package com.example.mycloverpayment.ui.listofinvoices

import android.accounts.Account
import android.app.Application
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
import javax.inject.Inject

class ListOfInvoicesViewModel @Inject constructor(app: Application) : BaseViewModel(app) {

    init {
        toastMessage.value = "Swipe Payment option not supported on Android devices"
    }


}