package com.example.mycloverpayment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.mycloverpayment.R


class LoginPage : Fragment() {

    lateinit var name : EditText
    lateinit var password : EditText
    lateinit var loginBtn : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = view.findViewById(R.id.et_email)
        password = view.findViewById(R.id.et_pass)
        loginBtn = view.findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {

            findNavController().navigate(R.id.action_loginPage_to_listOfInvoices)
        }
    }

}