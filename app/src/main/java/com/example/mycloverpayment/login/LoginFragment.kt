package com.example.mycloverpayment.login

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
import com.example.mycloverpayment.listofinvoices.ListOfInvoicesViewModel


class LoginFragment : Fragment() {

    lateinit var mViewModel: LoginViewModel
    lateinit var mDataBinding: FragmentLoginPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login_page,
            container,
            false
        )
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // DataBinding and ViewModel execution
        executeDataBindingAndViewModel()

        mDataBinding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_listOfInvoices)
        }
    }

    private fun executeDataBindingAndViewModel() {
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        mDataBinding.setVariable(BR.loginViewModel, mViewModel)
        mDataBinding.lifecycleOwner = this
        mDataBinding.executePendingBindings()
    }

}