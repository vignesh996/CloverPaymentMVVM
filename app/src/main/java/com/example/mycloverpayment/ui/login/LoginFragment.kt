package com.example.mycloverpayment.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseDialog
import com.example.mycloverpayment.base.BaseFragment
import com.example.mycloverpayment.databinding.FragmentLoginPageBinding
import com.example.mycloverpayment.helper.MainViewModelFactory
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import com.example.mycloverpayment.screens.dialog.DialogManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class LoginFragment : BaseFragment<FragmentLoginPageBinding, LoginViewModel>() {

    lateinit var loginPageBinding: FragmentLoginPageBinding
        @Inject
        lateinit var factory: ViewModelProvider.Factory


    override fun getViewModel(): LoginViewModel? =
            ViewModelProvider(this, factory).get(LoginViewModel::class.java)

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun getContentView(): Int = R.layout.fragment_login_page

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPageBinding =mDataBinding!!

        loginPageBinding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_listOfInvoices)
        }


    }

}