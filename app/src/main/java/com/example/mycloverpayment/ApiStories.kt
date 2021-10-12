package com.example.mycloverpayment

import com.example.mycloverpayment.carddetails.model.*
import com.example.mycloverpayment.customercarddetails.model.createordermodel.MerchandPaymentGatewayConfig
import com.example.mycloverpayment.customercarddetails.model.createordermodel.CreateCustomer
import com.example.mycloverpayment.customercarddetails.model.createordermodel.CreateCustomerResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiStories {

    @GET("/v3/merchants/{mId}")
    fun getResponse(@Header("Authorization")authToken :String, @Path("mId") id :String) : Call<ResponseBody>

    @POST("/v3/merchants/{mId}/customers")
    suspend fun createCustomer(@Header("Authorization")authToken :String, @Path("mId") id :String,@Body createCustomer: CreateCustomer) : CreateCustomerResponse

    @GET("/v3/merchants/{mId}/gateway")
    suspend fun getMerchantPaymentGateway(@Header("Authorization")authToken :String,@Path("mId") id :String) : MerchandPaymentGatewayConfig

    @POST("/v3/merchants/{mId}/authorizations")
    suspend fun createAuthorizationOfPayment(@Header("Authorization")authToken :String, @Path("mId") id :String)

    @GET("/pakms/apikey")
    suspend fun retrieveApiKay(@Header("Authorization")authToken :String) : RetriveKeyResponse

    @POST("/v1/tokens")
    suspend fun createCardToken(@Header("apikey")apiKey :String, @Body cardDetailsForToken : CardDetailsForToken) : CreateTokenResponse

    @POST("/v1/charges")
    suspend fun createCharge(@Header("Authorization")authToken :String, @Body createCharge: CreateCharge) : CreateChargeResponse
}