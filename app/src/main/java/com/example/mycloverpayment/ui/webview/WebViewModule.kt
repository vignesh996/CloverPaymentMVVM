package com.example.mycloverpayment.ui.webview

import androidx.lifecycle.ViewModelProvider
import com.example.mycloverpayment.helper.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class WebViewModule {

    // WebViewViewModel Factory provider
    @Provides
    fun provideViewModelProvider(viewModel: WebViewViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}