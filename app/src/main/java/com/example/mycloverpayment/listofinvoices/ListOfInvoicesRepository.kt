package com.example.mycloverpayment.listofinvoices

import com.example.mycloverpayment.network.ApiCall
import retrofit2.HttpException

class ListOfInvoicesRepository {

    suspend fun createAuthorizationOfPayment(authToken :String,mId: String)  {

        return try {
            val callApi = ApiCall.retrofitClient.createAuthorizationOfPayment(authToken,mId)
//            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
//            ApisResponse.Error(e)
        }
    }
}