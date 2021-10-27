package com.example.mycloverpayment.di

import com.example.mycloverpayment.MainActivity
import com.example.mycloverpayment.MainModule
import com.example.mycloverpayment.ui.listofinvoices.ListOfInvoices
import com.example.mycloverpayment.ui.listofinvoices.ListOfInvoicesModule
import com.example.mycloverpayment.ui.login.LoginFragment
import com.example.mycloverpayment.ui.login.LoginModule
import com.example.mycloverpayment.ui.webview.WebViewFragment
import com.example.mycloverpayment.ui.webview.WebViewModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules =[MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules =[LoginModule::class])
    abstract fun provideLoginFragment(): LoginFragment

    @ContributesAndroidInjector(modules =[ListOfInvoicesModule::class])
    abstract fun provideListOfInvoicesFragment(): ListOfInvoices

    @ContributesAndroidInjector(modules =[WebViewModule::class])
    abstract fun provideWebViewFragment(): WebViewFragment
}