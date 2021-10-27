package com.example.mycloverpayment

import android.accounts.Account
import android.app.Activity
import android.app.Application
import android.util.Log
import com.clover.sdk.util.CloverAccount
import com.example.mycloverpayment.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class CloverPaymentApp : Application(), HasAndroidInjector{

     var mAccount: Account? = null

    @Inject
    lateinit var mInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        // Declaring Merchant Account
        mAccount = getCloverAccount()

        Log.d("TAG", "onCreate: ${mAccount}")

        //Injecting Dagger AppComponent
        DaggerAppComponent.builder().application(this).build().inject(this)
    }

    // Getting Merchant Clover Account
    fun getCloverAccount(): Account? {
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(baseContext)
            if (mAccount == null) {
                return null
            }
        }
        return mAccount
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return mInjector
    }


}