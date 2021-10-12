package com.example.mycloverpayment.carddetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.clover.sdk.util.CloverAuth
import com.example.mycloverpayment.carddetails.model.CardDetailsForToken
import com.example.mycloverpayment.carddetails.model.CreateCharge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardDetailsViewModel : ViewModel() {


    suspend fun getCloverAuth(context: Context) : CloverAuth.AuthResult? {

        return withContext(Dispatchers.IO) {
            try {
                return@withContext  CloverAuth.authenticate(context.applicationContext)
            } catch (e: Exception) {
                Log.d("TAG", "Error authenticating", e)
            }
            return@withContext null
        }
    }

    fun retrieveApiKay(authToken :String) = liveData(Dispatchers.IO) {
        emit(CardDetailsRepository().retrieveApiKay(authToken))
    }

    fun createCardToken(apikey :String, cardDetailsForToken : CardDetailsForToken) = liveData(Dispatchers.IO) {
        emit(CardDetailsRepository().createCardToken(apikey, cardDetailsForToken))
    }

    fun createCharge(authToken :String, createCharge: CreateCharge) = liveData(Dispatchers.IO) {
        emit(CardDetailsRepository().createCharge(authToken,createCharge))
    }
}