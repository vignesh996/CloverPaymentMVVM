package com.example.mycloverpayment.ui.webview

import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import androidx.navigation.fragment.findNavController
import com.clover.sdk.util.CloverAuth
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseViewModel
import com.example.mycloverpayment.carddetails.model.CreateCharge
import com.example.mycloverpayment.model.ApisResponse
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WebViewViewModel(var context : Context) : BaseViewModel() {



     suspend fun getCloverAuth(): CloverAuth.AuthResult? {


        return withContext(Dispatchers.IO) {
            try {
                return@withContext CloverAuth.authenticate(context.applicationContext)
            } catch (e: Exception) {
                Log.d("TAG", "Error authenticating", e)
            }
            return@withContext null
        }
    }


    fun createCharge(authToken :String, createCharge: CreateCharge) = liveData(Dispatchers.IO) {
        emit(WebViewRepository().createCharge(authToken,createCharge))
    }

}