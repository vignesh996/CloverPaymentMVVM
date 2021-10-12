package com.example.mycloverpayment.customercarddetails

import android.accounts.Account
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.util.CloverAuth
import com.clover.sdk.v3.employees.EmployeeConnector
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.order.OrderConnector
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.customercarddetails.model.createordermodel.*
import com.example.mycloverpayment.databinding.FragmentCustomerCardDetailsBinding
import com.example.mycloverpayment.model.ApisResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomerCardDetails : Fragment() {

    lateinit var mViewModel: CustomerCardDetailsViewModel
    lateinit var mDataBinding: FragmentCustomerCardDetailsBinding
    private var mAccount: Account? = null
    private var mInventoryConnector: InventoryConnector? = null
    private var orderConnector: OrderConnector? = null
    private var employeeConnector : EmployeeConnector? = null
    private var authResult: CloverAuth.AuthResult? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // login Clover Account
        getCloverAccount()
        //connect with Inventory and Orders application
        connect()
        mDataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_customer_card_details,
                container,
                false
        )
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DataBinding and ViewModel execution
        executeDataBindingAndViewModel()

        init()

    }

    private fun init(){
        var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences("customer", Context.MODE_PRIVATE)
        var customerId = getSharedPreferences?.getString("customerId", "")

        if (!customerId.isNullOrEmpty()){
            Log.d("TAG", "createCustomer customer Id onviewcreated method : ${customerId}")
            moveToListOfInvoicesPage()
        }else{
            apiCall()
        }
    }

    private fun apiCall(){

        GlobalScope.launch(Dispatchers.Main) {

            getAuthToken()
            mViewModel.getEmployeeDetailsByEmployeeConnector(employeeConnector)

            var authToken = "Bearer " + authResult?.authToken
            getMerchantPaymentGateway(authToken,authResult?.merchantId!!)

            mDataBinding.createCustomerBtn.setOnClickListener {
                createCustomer(authToken,authResult?.merchantId!!)
            }
        }

    }

    private fun getMerchantPaymentGateway(authToken: String, merchantId: String) {
        mViewModel.getMerchantPaymentGateway(authToken,merchantId).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    Log.d("TAG", "apiCall mId : ${apiResponse.response.mid}")
                }
                is ApisResponse.Error -> {
                    Toast.makeText(activity, " Error : ${apiResponse.exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private suspend fun getAuthToken(){
        authResult = context?.let { mViewModel.getCloverAuth(it) }
    }

    private fun createCustomer(authToken: String, merchantId: String) {

        var createCustomer = createCustomerObject()
        Log.d("TAG", "createCustomer: auth token ${authResult?.authToken}")
        mViewModel.createCustomer(authToken, merchantId, createCustomer).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    customerIdSharedPreferences(apiResponse.response)
                    Log.d("TAG", "createCustomer: customer Id : ${apiResponse.response.id}")
                    moveToListOfInvoicesPage()
                }
                is ApisResponse.Error -> {
                    Toast.makeText(activity, " Error : ${apiResponse.exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun customerIdSharedPreferences(createCustomerResponse: CreateCustomerResponse) {
        var sharedPreferences = activity?.getSharedPreferences("customer", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        editor?.putString("customerId",createCustomerResponse.id)
        editor?.commit()
    }

    private fun moveToListOfInvoicesPage(){
//        findNavController().navigate(R.id.action_customerCardDetails_to_listOfInvoices)
    }

    private fun createCustomerObject() : CreateCustomer{

        var last4DigitNumber = mDataBinding.last4CardNumber.text.toString()
        last4CardNumberSharedPreference(last4DigitNumber)
        cardTypeSharedPreference(mDataBinding.cardType.text.toString())

        var card = Card(mDataBinding.customerExpDate.text.toString(),mDataBinding.first6CardNumber.text.toString(),mDataBinding.firstName.text.toString(),mDataBinding.last4CardNumber.text.toString(),mDataBinding.lastName.text.toString(),mDataBinding.cardType.text.toString())
        var cardList = ArrayList<Card>()
        cardList.add(card)

        var emailAddress = EmailAddresse(mDataBinding.email.text.toString())
        var emailList = ArrayList<EmailAddresse>()
        emailList.add(emailAddress)

        var firstName  = mDataBinding.firstName.text.toString()
        var lastName = mDataBinding.lastName.text.toString()

        var mobileNumber = PhoneNumber(mDataBinding.mobileNumber.text.toString())
        var mobileNumberList = ArrayList<PhoneNumber>()
        mobileNumberList.add(mobileNumber)

        var createCustomer = CreateCustomer(cardList,emailList,firstName,lastName,mobileNumberList)

        return createCustomer
    }

    private fun last4CardNumberSharedPreference(last4DigitNumber: String) {
        var sharedPreferences = activity?.getSharedPreferences("customer", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        editor?.putString("last4DigitNumber",last4DigitNumber)
        editor?.commit()
    }

    private fun cardTypeSharedPreference(cardType: String) {
        var sharedPreferences = activity?.getSharedPreferences("customer", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        editor?.putString("cardType",cardType)
        editor?.commit()
    }

    private fun executeDataBindingAndViewModel() {
        mViewModel = ViewModelProvider(this).get(CustomerCardDetailsViewModel::class.java)
        mDataBinding.setVariable(BR.customerCardDetailsViewModel,mViewModel)
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

    private fun connect() {
        disconnect()
        if (mAccount != null) {
            mInventoryConnector = InventoryConnector(activity, mAccount, null)
            mInventoryConnector!!.connect()
            employeeConnector =  EmployeeConnector(activity,mAccount, null)
            employeeConnector!!.connect()
            orderConnector = OrderConnector(activity, mAccount, null)
            orderConnector!!.connect()
        }
    }

    private fun disconnect() {
        if (mInventoryConnector != null) {
            mInventoryConnector!!.disconnect()
            mInventoryConnector = null
        }

        if (employeeConnector != null) {
            employeeConnector!!.disconnect()
            employeeConnector  = null
        }

        if (orderConnector != null) {
            orderConnector!!.disconnect()
            orderConnector = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "method onDestroy: called")
        mInventoryConnector!!.disconnect()
        employeeConnector!!.disconnect()
        orderConnector!!.disconnect()
    }
}