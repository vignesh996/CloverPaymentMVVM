package com.example.mycloverpayment.customercarddetails

import android.util.Log
import com.example.mycloverpayment.customercarddetails.model.createordermodel.MerchandPaymentGatewayConfig
import com.example.mycloverpayment.customercarddetails.model.createordermodel.CreateCustomer
import com.example.mycloverpayment.customercarddetails.model.createordermodel.CreateCustomerResponse
import com.example.mycloverpayment.helper.ApiCall
import com.example.mycloverpayment.model.ApisResponse
import retrofit2.HttpException

class CustomerCardDetailsRepository {

    suspend fun getMerchantPaymentGateway(authToken :String,mId: String)  : ApisResponse<MerchandPaymentGatewayConfig>{

        return try {
            val callApi = ApiCall.retrofitClient.getMerchantPaymentGateway(authToken,mId)
            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }


    suspend fun createCustomer(authToken :String,mId: String,createCustomer: CreateCustomer): ApisResponse<CreateCustomerResponse> {

        return try {
            val callApi = ApiCall.retrofitClient.createCustomer(authToken, mId, createCustomer)
            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }
}