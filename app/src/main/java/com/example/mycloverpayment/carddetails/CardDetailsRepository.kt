package com.example.mycloverpayment.carddetails

import android.util.Log
import com.example.mycloverpayment.carddetails.model.*
import com.example.mycloverpayment.network.ApiCall
import com.example.mycloverpayment.model.ApisResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class CardDetailsRepository {


    suspend fun retrieveApiKay(authToken :String)  : ApisResponse<RetriveKeyResponse>{

        return try {
            val callApi = ApiCall.retrofitClient.retrieveApiKay(authToken)
            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }

    suspend fun createCardToken(apikey :String, cardDetailsForToken : CardDetailsForToken)  : ApisResponse<CreateTokenResponse>{

        return try {
            val callApi = ApiCall.retrofitClientForToken.createCardToken(apikey, cardDetailsForToken)
            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
            val errorMessage = errorMessagefromapi(e)
            Log.d("TAG", "createCharge: ${errorMessage}")
            ApisResponse.CustomError(errorMessage!!)
        }
    }

    suspend fun createCharge(authToken :String, createCharge: CreateCharge)  : ApisResponse<CreateChargeResponse>{

        return try {
            val callApi = ApiCall.retrofitClientForCharge.createCharge(authToken,createCharge)
            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
            val errorMessage = errorMessagefromapi(e)
            Log.d("TAG", "createCharge: ${errorMessage}")
            ApisResponse.CustomError(errorMessage!!)

        }
    }

    private fun errorMessagefromapi(httpException: HttpException): String? {
        var errorMessage: String? = null
        val error = httpException.response()?.errorBody()

        try {

            val adapter = Gson().getAdapter(CardTokenErrorResponse::class.java)
            val errorParser = adapter.fromJson(error?.string())
            errorMessage = errorParser.error.message
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            return errorMessage
        }
    }


}