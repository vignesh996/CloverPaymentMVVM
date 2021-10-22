package com.example.mycloverpayment.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    var toastMessage = MutableLiveData<String>()

}