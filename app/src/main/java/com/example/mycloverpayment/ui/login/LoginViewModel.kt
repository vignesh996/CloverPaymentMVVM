package com.example.mycloverpayment.ui.login



import android.app.Application
import android.content.Context
import android.util.Log
import com.example.mycloverpayment.base.BaseViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(app: Application) : BaseViewModel(app) {

init {
    Log.d("TAG", "view model called ")
}

}