package com.example.mycloverpayment.webview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mycloverpayment.carddetails.model.CreateCharge
import kotlinx.coroutines.Dispatchers

class WebViewViewModel :ViewModel() {

    fun createCharge(authToken :String, createCharge: CreateCharge) = liveData(Dispatchers.IO) {
        emit(WebViewRepository().createCharge(authToken,createCharge))
    }
}