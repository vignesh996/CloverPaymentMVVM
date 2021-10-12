package com.example.mycloverpayment.helper

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent


class MyJavaScriptInterface {
    private var ctx: Context? = null

     constructor(ctx: Context?) {
        this.ctx = ctx
    }

    @JavascriptInterface
    fun getToken(token : String) {
        RxBus.publish(RxBusEvent.Token(token))
    }


}