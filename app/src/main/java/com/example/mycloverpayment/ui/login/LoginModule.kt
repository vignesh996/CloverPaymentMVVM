package com.example.mycloverpayment.ui.login

import androidx.lifecycle.ViewModelProvider
import com.example.mycloverpayment.helper.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class LoginModule {

    // LoginViewModel Factory provider
    @Provides
    fun provideViewModelProvider(viewModel: LoginViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}