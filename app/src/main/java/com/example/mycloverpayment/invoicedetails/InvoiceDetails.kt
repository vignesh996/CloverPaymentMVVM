package com.example.mycloverpayment.invoicedetails

import android.accounts.Account
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.v1.Intents
import com.clover.sdk.v1.Intents.ACTION_CLOVER_PAY
import com.clover.sdk.v1.Intents.ACTION_SECURE_PAY
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.order.*
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.databinding.FragmentInvoiceDetailsBinding
import com.example.mycloverpayment.model.PaymentOrder
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoiceDetails : Fragment() {

    lateinit var mViewModel: InvoiceDetailsViewModel
    lateinit var mDataBinding: FragmentInvoiceDetailsBinding
    lateinit var payButton: Button
    private var mAccount: Account? = null
    private var mInventoryConnector: InventoryConnector? = null
    private var orderConnector: OrderConnector? = null
    lateinit var mOrder: Order
    private lateinit var invoiceDetail : PaymentOrder
    private var invoicePosition: Int = 0
    private val cardEntryMethodsAllowed =
            Intents.CARD_ENTRY_METHOD_MAG_STRIPE or Intents.CARD_ENTRY_METHOD_ICC_CONTACT or Intents.CARD_ENTRY_METHOD_NFC_CONTACTLESS or Intents.CARD_ENTRY_METHOD_MANUAL


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // login Clover Account
        getCloverAccount()
        //connect with Inventory and Orders application
        connect()
        mDataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_invoice_details,
                container,
                false
        )
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payButton = view.findViewById(R.id.payBtn)
        // DataBinding and ViewModel execution
        executeDataBindingAndViewModel()
        //Hide UI Visibility
        hideUI()
        //Update on clicked Invoice Details
        updateInvoiceDetails()

    }

    private fun  hideUI(){
        mDataBinding.tvInvoiceDetails.visibility = View.INVISIBLE
        mDataBinding.tvCustomerId.visibility = View.INVISIBLE
        mDataBinding.tvCustomerName.visibility =View.INVISIBLE
        mDataBinding.tvInvoiceAmount.visibility = View.INVISIBLE
        mDataBinding.amountSymbol.visibility = View.INVISIBLE
        mDataBinding.tvPaymentOrderId.visibility = View.INVISIBLE
        mDataBinding.tvStatus.visibility = View.INVISIBLE
        mDataBinding.payBtn.visibility =  View.INVISIBLE
    }

    private fun  displayUI(){
        mDataBinding.tvInvoiceDetails.visibility = View.VISIBLE
        mDataBinding.tvCustomerId.visibility = View.VISIBLE
        mDataBinding.tvCustomerName.visibility = View.VISIBLE
        mDataBinding.tvInvoiceAmount.visibility = View.VISIBLE
        mDataBinding.amountSymbol.visibility = View.VISIBLE
        mDataBinding.tvPaymentOrderId.visibility = View.VISIBLE
        mDataBinding.tvStatus.visibility = View.VISIBLE
        mDataBinding.payBtn.visibility =  View.VISIBLE

    }

    private fun updateInvoiceDetails() {
        RxBus.listen(RxBusEvent.Details::class.java).subscribe { details ->
            displayUI()
            setInvoiceDetailsUI(details)

            payButton.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    var detail = lunchBackgroundThread(details.invoiceDetail, details.position)
                    RxBus.publish(RxBusEvent.InvoiceDetail(detail, details.position))
                }
            }
        }
    }
    

    suspend fun lunchBackgroundThread(invoiceDetails: PaymentOrder, position: Int): PaymentOrder {
            return withContext(Dispatchers.IO) {

               var details = createOrder(invoiceDetails)
                invoiceDetail =  invoiceDetails
                invoicePosition = position
                return@withContext details
            }
        }

    private fun createOrder(details: PaymentOrder): PaymentOrder {

            mOrder = orderConnector!!.createOrder(Order())
            mOrder.setPayType(PayType.FULL)
            details.uniqueId = mOrder.id
            addCustomLineItem(details)

            connectToCloverPaymentPage(details)

        return details
    }

    private fun addCustomLineItem(details: PaymentOrder) {
        var customLineItem = LineItem().apply {
            price = details.amount
            note = "Payment Made by Vignesh"
        }
        orderConnector!!.addCustomLineItem(details.uniqueId, customLineItem, false)
    }

    private fun connectToCloverPaymentPage(details: PaymentOrder) {

        Intent(ACTION_CLOVER_PAY).apply {
            this.putExtra(Intents.EXTRA_ORDER_ID, details.uniqueId)
            this.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, cardEntryMethodsAllowed)
            var myOrder = orderConnector!!.getOrder(details.uniqueId)

            checkPaymentState(this, myOrder,details)

        }
    }

    private fun checkPaymentState(intent: Intent, myOrder: Order, details: PaymentOrder) {
        if (myOrder.hasPaymentState()) {
            if (myOrder.paymentState == PaymentState.PAID) {
                Log.d("TAG", "connectToCloverPayment one: payment paid already")
            } else if (mOrder.paymentState == PaymentState.OPEN || mOrder.paymentState == PaymentState.PARTIALLY_PAID) {
                Log.d("TAG", "connectToCloverPayment one: payment open state")
                resultLauncher.launch(intent)
            }
        }else {
            resultLauncher.launch(intent)
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "Amount Paid Successfully", Toast.LENGTH_SHORT).show()
            val data: Intent? = result.data
            updatePaymentStatus()

            GlobalScope.launch(Dispatchers.IO) {

               var orderDetail = orderConnector!!.getOrder(invoiceDetail.uniqueId)

                if (orderDetail.payments.get(0).tender.label == "Cash"){
                      orderDetail.clearPayments()
                    orderDetail.clearState()
                    Log.d("TAG", "Cash Payment not allowed ")

                }else if (orderDetail.payments.get(0).tender.label == "Card"){


                }

            }

        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            paymentFailedDialog()

        }
    }



    private fun updatePaymentStatus(){
        invoiceDetail.paymentStatus = "paid"
        mDataBinding.paymentStatus.text = "paid"
        mDataBinding.payBtn.visibility = View.INVISIBLE
        mDataBinding.paymentStatus.setTextColor(Color.parseColor("#4CAF50"))
        RxBus.publish(RxBusEvent.PaymentStatus(invoiceDetail, invoicePosition))
    }

    private fun setInvoiceDetailsUI(details: RxBusEvent.Details) {
        mDataBinding.customerName.text = details.invoiceDetail.customerName
        mDataBinding.customerId.text = details.invoiceDetail.customerId
        mDataBinding.invoiceAmount.text = details.invoiceDetail.amount.toString()
        mDataBinding.uniqueId.text = details.invoiceDetail.uniqueId
        mDataBinding.paymentStatus.text = details.invoiceDetail.paymentStatus
        if (details.invoiceDetail.paymentStatus.toUpperCase() =="PAID"){
            mDataBinding.paymentStatus.setTextColor(Color.parseColor("#4CAF50"))
            mDataBinding.payBtn.visibility = View.INVISIBLE
        }else{
            mDataBinding.paymentStatus.setTextColor(Color.parseColor("#E1AF1B"))
            mDataBinding.payBtn.visibility = View.VISIBLE
        }
    }

    fun getCloverAccount() {
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
            orderConnector = OrderConnector(activity, mAccount, null)
            orderConnector!!.connect()
        }
    }

    private fun disconnect() {
        if (mInventoryConnector != null) {
            mInventoryConnector!!.disconnect()
            mInventoryConnector = null
        }

        if (orderConnector != null) {
            orderConnector!!.disconnect()
            orderConnector = null
        }
    }

    private fun executeDataBindingAndViewModel() {
        mViewModel = ViewModelProvider(this).get(InvoiceDetailsViewModel::class.java)
        mDataBinding.setVariable(BR.invoiceDetailsViewModel, mViewModel)
        mDataBinding.lifecycleOwner = this
        mDataBinding.executePendingBindings()
    }

    private  fun paymentFailedDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Payment Failed")
        builder.setCancelable(false)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> })
        builder.create()
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mInventoryConnector!!.disconnect()
        orderConnector!!.disconnect()
    }

}