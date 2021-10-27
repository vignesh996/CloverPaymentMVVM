package com.example.mycloverpayment.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {

    var toastMessage = MutableLiveData<String>()

}