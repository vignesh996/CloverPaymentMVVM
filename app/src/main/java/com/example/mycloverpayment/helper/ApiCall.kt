package com.example.mycloverpayment.helper

import android.icu.util.TimeUnit
import com.example.mycloverpayment.ApiStories
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiCall {

    /*This is used to create retrofit object for performing network opration*/

    val retrofitClient : ApiStories by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(50,java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return@lazy retrofit.create(ApiStories::class.java)
    }

    val retrofitClientForToken : ApiStories by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(50,java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL_TOKEN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        return@lazy retrofit.create(ApiStories::class.java)
    }

    val retrofitClientForCharge : ApiStories by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(50,java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL_CHARGE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        return@lazy retrofit.create(ApiStories::class.java)
    }
}