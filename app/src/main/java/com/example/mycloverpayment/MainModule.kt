package com.example.mycloverpayment

import androidx.lifecycle.ViewModelProvider
import com.example.mycloverpayment.helper.ViewModelProviderFactory
import com.example.mycloverpayment.ui.listofinvoices.ListOfInvoicesViewModel
import dagger.Module
import dagger.Provides

@Module
class MainModule {


    @Provides
    fun provideViewModelProvider(viewModel: MainViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}