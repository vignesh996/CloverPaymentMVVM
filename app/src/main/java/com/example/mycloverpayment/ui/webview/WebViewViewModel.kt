package com.example.mycloverpayment.ui.webview

import android.app.Application
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
import javax.inject.Inject

class WebViewViewModel @Inject constructor(private val webViewRepository: WebViewRepository,
                                           app: Application) : BaseViewModel(app) {


    //  Method for Getting Runtime Merchant's Clover Authentication
     suspend fun getCloverAuth(): CloverAuth.AuthResult? {

        return withContext(Dispatchers.IO) {
            try {
//                return@withContext CloverAuth.authenticate(context.applicationContext)
                return@withContext CloverAuth.authenticate(getApplication())
            } catch (e: Exception) {
                Log.d("TAG", "Error authenticating", e)
            }
            return@withContext null
        }
    }

    // Method for Make Payments
    fun createCharge(authToken :String, createCharge: CreateCharge) = liveData(Dispatchers.IO) {
        emit(webViewRepository.createCharge(authToken,createCharge))
    }

}