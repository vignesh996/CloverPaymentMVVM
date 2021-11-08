package com.example.mycloverpayment.ui.listofinvoices

import androidx.lifecycle.ViewModelProvider
import com.example.mycloverpayment.helper.ViewModelProviderFactory
import dagger.Module
import dagger.Provides


@Module
class ListOfInvoicesModule {

    // ListOfInvoicesViewModel Factory provider
    @Provides
    fun provideViewModelProvider(viewModel: ListOfInvoicesViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(viewModel)
    }
}