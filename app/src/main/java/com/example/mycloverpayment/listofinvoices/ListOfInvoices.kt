package com.example.mycloverpayment.listofinvoices

import android.accounts.Account
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.v1.Intents
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.order.*
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.databinding.FragmentListOfInvoicesBinding
import com.example.mycloverpayment.listofinvoices.adapter.InvoicesAdapter
import com.example.mycloverpayment.model.InvoiceUpdate
import com.example.mycloverpayment.model.PaymentOrder
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListOfInvoices : Fragment(), InvoicesAdapter.OnServiceClickListener {

    lateinit var mViewModel: ListOfInvoicesViewModel
    lateinit var mDataBinding: FragmentListOfInvoicesBinding
    var adapter = InvoicesAdapter()
    lateinit var invoicesList : List<PaymentOrder>
    private var mAccount: Account? = null
    private var mInventoryConnector: InventoryConnector? = null
    private var orderConnector: OrderConnector? = null
    private lateinit var invoiceDetail: PaymentOrder
    private var invoicePosition: Int = 0
    lateinit var mOrder: Order
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
            R.layout.fragment_list_of_invoices,
            container,
            false
        )
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // DataBinding and ViewModel execution
        executeDataBindingAndViewModel()
        // Adapting recyclerView
        executeRecyclerView()
        // Observe and Update payment Id
        observePaymentId()
        // Observe and Update payment Status
        observePaymentStatus()



    }

    private fun observePaymentId(){
        RxBus.listen(RxBusEvent.PaymentId::class.java).subscribe {
            invoicesList[it.position].uniqueId = it.invoiceDetail.uniqueId
            adapter.refreshItems(invoicesList)
        }
    }

    private fun observePaymentStatus(){
        RxBus.listen(RxBusEvent.PaymentStatus::class.java).subscribe {
            invoicesList[it.position].paymentStatus = it.invoiceDetail.paymentStatus
            invoicesList[it.position].amount = it.invoiceDetail.amount
            adapter.refreshItems(invoicesList)
        }
    }

    private fun executeRecyclerView(){
        invoicesList = getInvoiceList()
        var recyclerView = mDataBinding.recyclerView
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.setLayoutManager(linearLayoutManager)
        recyclerView.setAdapter(adapter)
        adapter.setOnClickListener(this)
        adapter.refreshItems(invoicesList)
    }

    fun getInvoiceList(): ArrayList<PaymentOrder> {
        var invoicesList = ArrayList<PaymentOrder>()

        invoicesList.add(PaymentOrder("454","Vignesh","78945",500L,"open",""))
        invoicesList.add(PaymentOrder("589","Arun","78946",700L,"open",""))
        invoicesList.add(PaymentOrder("987","Suresh","78947",900L,"open",""))
        invoicesList.add(PaymentOrder("354","Jaya","78948",1500L,"open",""))
        invoicesList.add(PaymentOrder("986","Kamal","78949",5000L,"open",""))
        invoicesList.add(PaymentOrder("234","Partha","78910",2500L,"open",""))
        invoicesList.add(PaymentOrder("666","Murty","78911",7500L,"open",""))
        invoicesList.add(PaymentOrder("222","Vijay","78912",9500L,"open",""))
        invoicesList.add(PaymentOrder("111","Magesh","78913",6500L,"open",""))
        invoicesList.add(PaymentOrder("444","Muthu","78914",700L,"open",""))
        invoicesList.add(PaymentOrder("333","Surya","78915",500L,"open",""))
        invoicesList.add(PaymentOrder("555","Latchu","78916",800L,"open",""))
        invoicesList.add(PaymentOrder("777","Mugu","78917",200L,"open",""))

        return invoicesList
    }

    private fun executeDataBindingAndViewModel() {
        mViewModel = ViewModelProvider(this).get(ListOfInvoicesViewModel::class.java)
        mDataBinding.setVariable(BR.listOfInvoicesViewModel, mViewModel)
        mDataBinding.lifecycleOwner = this
        mDataBinding.executePendingBindings()
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


    override fun onPayBtnClicked(invoiceList: ArrayList<PaymentOrder>, position: Int, statusView: TextView) {
        var uniqueId = invoiceList[position].uniqueId
        var customerName = invoiceList[position].customerName
        var customerId = invoiceList[position].customerId
        var amount = invoiceList[position].amount
        var paymentStatus = invoiceList[position].paymentStatus
        var cloverOrderId = invoiceList[position].cloverOrderId

        var invoiceDetails =  PaymentOrder(uniqueId,customerName,customerId,amount,paymentStatus,cloverOrderId)

        GlobalScope.launch(Dispatchers.Main) {
            var detail = lunchBackgroundThread(invoiceDetails,position)
            RxBus.publish(RxBusEvent.PaymentId(detail,position))
        }
    }

    suspend fun lunchBackgroundThread(invoiceDetails: PaymentOrder, position: Int): PaymentOrder {

        return withContext(Dispatchers.IO) {
            invoiceDetail = invoiceDetails
            invoicePosition = position
            var details = createOrder(invoiceDetails,position)
            return@withContext details
        }
    }

    private fun createOrder(details: PaymentOrder, position: Int): PaymentOrder {

        if (details.cloverOrderId.isNullOrEmpty()){
            mOrder = orderConnector!!.createOrder(Order())
            mOrder.setPayType(PayType.FULL)
            details.uniqueId = mOrder.id
            details.cloverOrderId = mOrder.id
            setCloverOrderId(details,position)
            addCustomLineItem(details)
        }else{
            mOrder= orderConnector!!.getOrder(details.uniqueId)
        }
        connectToCloverPaymentPage(details)

        return details
    }

    private  fun setCloverOrderId(details: PaymentOrder, position: Int) {
     invoicesList[position].cloverOrderId = details.cloverOrderId
    }


    private fun addCustomLineItem(details: PaymentOrder) {
        var customLineItem = LineItem().apply {
            price = details.amount
            note = "Payment Made by Vignesh"
            this.name = details.customerName
        }
        orderConnector!!.addCustomLineItem(details.uniqueId, customLineItem, false)
    }

    private fun connectToCloverPaymentPage(details: PaymentOrder) {
        Intent(Intents.ACTION_CLOVER_PAY).apply {
            this.putExtra(Intents.EXTRA_ORDER_ID, details.uniqueId)
            this.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, cardEntryMethodsAllowed)
            var myOrder = orderConnector!!.getOrder(details.uniqueId)

            checkPaymentState(this, myOrder, details)

        }
    }

    private fun checkPaymentState(intent: Intent, myOrder: Order, details: PaymentOrder) {
        if (myOrder.hasPaymentState()) {
            if (myOrder.paymentState == PaymentState.PAID) {
            } else if (mOrder.paymentState == PaymentState.OPEN || mOrder.paymentState == PaymentState.PARTIALLY_PAID) {
                resultLauncher.launch(intent)
            }
        } else {
            resultLauncher.launch(intent)
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            GlobalScope.launch(Dispatchers.Main) {
                var invoiceUpdate = getOrderPaymentState()
                //Updating payment status and Hide payButton On UI
                updatePaymentStatus(invoiceUpdate)
            }

        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            paymentFailedDialog()
        }
    }


    suspend fun getOrderPaymentState(): InvoiceUpdate? {

        return withContext(Dispatchers.IO) {
            var orderDetail = orderConnector!!.getOrder(invoiceDetail.uniqueId)
            var invoiceUpdate = orderPaymentState(orderDetail)
            return@withContext invoiceUpdate
        }

    }

    private fun orderPaymentState(orderDetail: Order): InvoiceUpdate? {

        var invoiceUpdate: InvoiceUpdate? = null

        if (orderDetail.payments.get(0).tender.label == "Cash") {
            orderDetail.clearPayments()
            orderDetail.clearState()
            Log.d("TAG", "Cash Payment not allowed ")

        } else if (orderDetail.payments.get(0).tender.label == "Credit Card") {

            if (orderDetail.total == orderDetail.payments.get(0).amount){
                invoiceUpdate = InvoiceUpdate("paid",0, "Payment Successfully done" )
                Log.d("TAG", "Cash Payment done fully paid")
            }else{
                invoiceUpdate = partialPayment(orderDetail)

            }
        }

        return invoiceUpdate
    }

    private fun partialPayment(orderDetail: Order): InvoiceUpdate? {
        var invoiceUpdate: InvoiceUpdate? = null
        var amount = 0L
        for (i in 0..orderDetail.payments.size-1){
            amount = amount + orderDetail.payments.get(i).amount
        }

        if (orderDetail.total == amount){
            invoiceUpdate = InvoiceUpdate("paid",0, "Payment Successfully done")
            Log.d("TAG", "Cash Payment done fully paid")
        }else{
            var pendingAmount = orderDetail.total - amount
            invoiceUpdate = InvoiceUpdate("partially paid",pendingAmount,"Partial payment Successfully done" )
            Log.d("TAG", "Cash Payment done partially paid")
        }

        amount = 0

        return invoiceUpdate
    }

    private fun paymentFailedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Payment is failed. Please try again")
        builder.setCancelable(false)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> })
        builder.create()
        builder.show()
    }

    private fun updatePaymentStatus(invoiceUpdate: InvoiceUpdate?) {
        Toast.makeText(requireContext(), "${invoiceUpdate?.message}", Toast.LENGTH_SHORT).show()
        invoiceUpdate?.paymentStatus
        invoiceDetail.paymentStatus = invoiceUpdate?.paymentStatus.toString()
        if (invoiceUpdate?.amount != 0L){
            invoiceDetail.amount = invoiceUpdate?.amount!!
        }
        RxBus.publish(RxBusEvent.PaymentStatus(invoiceDetail, invoicePosition))
    }

    override fun onDestroy() {
        super.onDestroy()
        mInventoryConnector!!.disconnect()
        orderConnector!!.disconnect()
    }
}