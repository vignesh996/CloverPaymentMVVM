package com.example.mycloverpayment.helper

import android.util.Log

class TokenCallBack {

    private var tokenCallBackInterface: TokenCallBackInterface? = null

    fun setCallBackInterface(callBackInterface: TokenCallBackInterface) {
        this.tokenCallBackInterface = callBackInterface
    }
    interface TokenCallBackInterface {
        fun resultLauncher(token : String)
    }

    fun callback(token : String){
        Log.d("TAG", "callback: ${token}")
        tokenCallBackInterface?.resultLauncher(token)

    }
}