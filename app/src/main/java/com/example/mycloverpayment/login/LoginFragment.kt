package com.example.mycloverpayment.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseFragment
import com.example.mycloverpayment.databinding.FragmentListOfInvoicesBinding
import com.example.mycloverpayment.databinding.FragmentLoginPageBinding
import com.example.mycloverpayment.helper.MainViewModelFactory
import com.example.mycloverpayment.listofinvoices.ListOfInvoicesViewModel


class LoginFragment : BaseFragment<FragmentLoginPageBinding,LoginViewModel>() {

    lateinit var loginPageBinding: FragmentLoginPageBinding
    lateinit var viewModelFactory: MainViewModelFactory


    override fun getViewModel(): LoginViewModel? =
            ViewModelProvider(this,  viewModelFactory).get(LoginViewModel::class.java)

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun getContentView(): Int = R.layout.fragment_login_page

    override fun onAttach(context: Context) {
        viewModelFactory = MainViewModelFactory(requireContext())
        super.onAttach(context)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPageBinding =mDataBinding!!

        loginPageBinding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_listOfInvoices)
        }
    }



}