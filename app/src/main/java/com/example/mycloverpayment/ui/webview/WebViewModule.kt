package com.example.mycloverpayment.ui.webview

import androidx.lifecycle.ViewModelProvider
import com.example.mycloverpayment.helper.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class WebViewModule {

    @Provides
    fun provideViewModelProvider(viewModel: WebViewViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}