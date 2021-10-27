package com.example.mycloverpayment

import android.app.Application
import android.util.Log
import com.example.mycloverpayment.base.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(app: Application) : BaseViewModel(app) {

    init {
        Log.d("TAG", "view model main called ")
    }
}