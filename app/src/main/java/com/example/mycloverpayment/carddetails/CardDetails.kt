package com.example.mycloverpayment.carddetails

import android.accounts.Account
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.util.CloverAuth
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.carddetails.model.CardDetailsForToken
import com.example.mycloverpayment.carddetails.model.CardX
import com.example.mycloverpayment.carddetails.model.CreateCharge
import com.example.mycloverpayment.databinding.FragmentCardDetailsBinding
import com.example.mycloverpayment.model.ApisResponse
import com.example.mycloverpayment.model.InvoiceDetail
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import kotlinx.coroutines.*
import java.util.*


class CardDetails : Fragment() {

    lateinit var mViewModel: CardDetailsViewModel
    lateinit var mDataBinding: FragmentCardDetailsBinding
    private var mAccount: Account? = null
    private var authResult: CloverAuth.AuthResult? = null
    private lateinit var apiAccessKey: String
    private val args: CardDetailsArgs by navArgs()
    lateinit var invoiceDetail: InvoiceDetail

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // login Clover Account
        getCloverAccount()
        mDataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_card_details,
                container,
                false
        )
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // DataBinding and ViewModel execution
        executeDataBindingAndViewModel()

        invoiceDetail = args.invoiceDetail

        GlobalScope.launch(Dispatchers.Main) {
            authResult = getCloverAuth()
            var authToken = "Bearer " + authResult?.authToken
            retrieveApiKay(authToken)

            mDataBinding.payBtn.setOnClickListener {
                cardDeatilsVerification()
            }
        }

    }

    private fun cardDeatilsVerification() {
        if (mDataBinding.cardNumber.text.isNullOrEmpty() || mDataBinding.cvv.text.isNullOrEmpty() || mDataBinding.expMonth.text.isNullOrEmpty() || mDataBinding.expYear.text.isNullOrEmpty()) {
            Toast.makeText(context, "${getString(R.string.mandatory_error)}", Toast.LENGTH_SHORT).show()
        } else if (mDataBinding.cardNumber.text.length in 11..15) {
            if (mDataBinding.cvv.text.length in 3..4) {
                if (mDataBinding.expMonth.text.length == 2 && mDataBinding.expMonth.text.toString().toInt() <= 12) {
                    var year = getLast2CharsOfString(Calendar.getInstance().get(Calendar.YEAR).toString(), 2)
                    if (mDataBinding.expYear.text.length == 2 && mDataBinding.expYear.text.toString().toInt() >= year!!.toInt()) {
                        if (mDataBinding.expYear.text.toString().toInt() == year!!.toInt()) {
                            if (mDataBinding.expMonth.text.toString().toInt() > Calendar.getInstance().get(Calendar.MONTH) + 1) {
                                progressBar()
                                var cardDetailsForToken = getCardDetails()
                                createCardToken(apiAccessKey, cardDetailsForToken)
                            } else {
                                mDataBinding.expMonth.error = "Exp.Month Not Valid"
                            }
                        } else {
                            progressBar()
                            var cardDetailsForToken = getCardDetails()
                            createCardToken(apiAccessKey, cardDetailsForToken)
                        }
                    } else {
                        mDataBinding.expYear.error = getString(R.string.enter_valid_year)
                    }
                } else {
                    mDataBinding.expMonth.error = getString(R.string.enter_valid_month)
                }
            } else {
                mDataBinding.cvv.error = getString(R.string.enter_valid_cvv)
            }
        } else {
            mDataBinding.cardNumber.error = getString(R.string.valid_card_number)
        }
    }

    private fun createCardToken(apiAccessKey: String, cardDetailsForToken: CardDetailsForToken) {

        mViewModel.createCardToken(apiAccessKey, cardDetailsForToken).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    var cardToken = apiResponse.response.id
                    var amount = invoiceDetail.amount.toInt() * 100
                    createCharge(CreateCharge(amount, "usd", "ecom", cardToken))
                }
                is ApisResponse.CustomError -> {
                    progressBar()
                    Log.d("TAG", "onPayBtnClicked token id CustomError called")
                    Toast.makeText(activity, "${apiResponse.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun createCharge(createCharge: CreateCharge) {
        var authToken = "Bearer " + authResult?.authToken
        mViewModel.createCharge(authToken, createCharge).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    progressBar()
                    findNavController().navigate(R.id.action_cardDetails_to_listOfInvoices2)
                    RxBus.publish(RxBusEvent.Result(apiResponse.response.id, apiResponse.response.status, invoiceDetail.position))
                    Toast.makeText(context, "Payment Successfully Paid", Toast.LENGTH_SHORT).show()
                }
                is ApisResponse.CustomError -> {
                    progressBar()
                    Toast.makeText(activity, "${apiResponse.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getCardDetails(): CardDetailsForToken {
        var cardNumber = mDataBinding.cardNumber.text.toString()
        var cvv = mDataBinding.cvv.text.toString()
        var expMonth = mDataBinding.expMonth.text.toString()
        var expYear = mDataBinding.expYear.text.toString()

        return CardDetailsForToken(CardX(cvv, expMonth, expYear, cardNumber))
    }

    private fun retrieveApiKay(authToken: String) {
        mViewModel.retrieveApiKay(authToken).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    apiAccessKey = apiResponse.response.apiAccessKey
                }
                is ApisResponse.Error -> {
                    Toast.makeText(activity, " Error : ${apiResponse.exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun executeDataBindingAndViewModel() {
        mViewModel = ViewModelProvider(this).get(CardDetailsViewModel::class.java)
        mDataBinding.setVariable(BR.cardDetailsViewModel, mViewModel)
        mDataBinding.lifecycleOwner = this
        mDataBinding.executePendingBindings()
    }

    private fun getCloverAccount() {
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(activity)
            if (mAccount == null) {
                return
            }
        }
    }

    private suspend fun getCloverAuth(): CloverAuth.AuthResult? {

        return withContext(Dispatchers.IO) {
            try {
                return@withContext CloverAuth.authenticate(requireContext().applicationContext)
            } catch (e: Exception) {
                Log.d("TAG", "Error authenticating", e)
            }
            return@withContext null
        }
    }

    private fun progressBar() {
        if (mDataBinding.progressbarLayout.isVisible) {
            mDataBinding.progressbarLayout.visibility = View.GONE
            mDataBinding.payBtn.isClickable = true
            mDataBinding.cardNumber.visibility = View.VISIBLE
            mDataBinding.payBtn.visibility = View.VISIBLE
            mDataBinding.cvv.visibility = View.VISIBLE
            mDataBinding.expMonth.visibility = View.VISIBLE
            mDataBinding.expYear.visibility = View.VISIBLE

        } else {
            mDataBinding.progressbarLayout.visibility = View.VISIBLE
            mDataBinding.payBtn.isClickable = false
            mDataBinding.cardNumber.visibility = View.INVISIBLE
            mDataBinding.payBtn.visibility = View.INVISIBLE
            mDataBinding.cvv.visibility = View.INVISIBLE
            mDataBinding.expMonth.visibility = View.INVISIBLE
            mDataBinding.expYear.visibility = View.INVISIBLE

        }

    }

    private fun getLast2CharsOfString(str: String, n: Int): String? {
        var lastnChars = str
        if (lastnChars.length > n) {
            lastnChars = lastnChars.substring(lastnChars.length - n, lastnChars.length)
        }
        return lastnChars
    }
}