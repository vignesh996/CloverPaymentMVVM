package com.example.mycloverpayment.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycloverpayment.ui.listofinvoices.ListOfInvoicesViewModel
import com.example.mycloverpayment.ui.login.LoginViewModel
import com.example.mycloverpayment.ui.webview.WebViewViewModel

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListOfInvoicesViewModel::class.java)){
            return  ListOfInvoicesViewModel(context) as T
        }

        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return  LoginViewModel(context) as T
        }

        if (modelClass.isAssignableFrom(WebViewViewModel::class.java)){
            return  WebViewViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}