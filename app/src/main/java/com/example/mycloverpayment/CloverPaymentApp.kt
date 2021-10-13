package com.example.mycloverpayment

import android.accounts.Account
import android.app.Application
import android.util.Log
import com.clover.sdk.util.CloverAccount

class CloverPaymentApp : Application() {

     var mAccount: Account? = null

    override fun onCreate() {
        super.onCreate()
     mAccount = getCloverAccount()
        Log.d("TAG", "onCreate: ${mAccount}")
    }

    fun getCloverAccount(): Account? {
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(baseContext)
            if (mAccount == null) {
                return null
            }
        }
        return mAccount
    }

}