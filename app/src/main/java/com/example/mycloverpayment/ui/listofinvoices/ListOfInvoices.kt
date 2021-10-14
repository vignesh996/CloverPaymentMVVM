package com.example.mycloverpayment.ui.listofinvoices

import android.accounts.Account
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clover.connector.sdk.v3.PaymentConnector
import com.clover.sdk.v1.Intents
import com.clover.sdk.v3.connector.ExternalIdUtils
import com.clover.sdk.v3.order.*
import com.clover.sdk.v3.payments.TipMode
import com.clover.sdk.v3.remotepay.SaleRequest
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.CloverPaymentApp
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseFragment
import com.example.mycloverpayment.databinding.FragmentListOfInvoicesBinding
import com.example.mycloverpayment.helper.MainViewModelFactory
import com.example.mycloverpayment.helper.StaticInvoiceList
import com.example.mycloverpayment.ui.listofinvoices.adapter.InvoicesAdapter
import com.example.mycloverpayment.model.InvoiceDetail
import com.example.mycloverpayment.model.PaymentOrder
import com.example.mycloverpayment.paymentconnector.MyPaymentConnector
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent


class ListOfInvoices : BaseFragment<FragmentListOfInvoicesBinding, ListOfInvoicesViewModel>(),
        InvoicesAdapter.OnServiceClickListener {

    lateinit var listOfInvoicesBinding: FragmentListOfInvoicesBinding
    var adapter = InvoicesAdapter()
    private var mAccount: Account? = null
    private lateinit var paymentConnector: PaymentConnector
    lateinit var viewModelFactory: MainViewModelFactory

    override fun onAttach(context: Context) {
        viewModelFactory = MainViewModelFactory(requireContext())
        super.onAttach(context)

    }

    override fun getViewModel(): ListOfInvoicesViewModel? =
            ViewModelProvider(this,  viewModelFactory).get(ListOfInvoicesViewModel::class.java)

    override fun getBindingVariable(): Int = BR.listOfInvoicesViewModel

    override fun getContentView(): Int = R.layout.fragment_list_of_invoices

    companion object {
        private var invoicesList = StaticInvoiceList().getInvoiceList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        listOfInvoicesBinding = mDataBinding!!
        //get current account
        getCurrentAccount()
        // Adapting recyclerView
        executeRecyclerView(view)
        //Update ResultData
        observeResult()

    }

    private fun getCurrentAccount() {
        mAccount = CloverPaymentApp().mAccount
    }


    private fun observeResult() {
        var paymentStatus = RxBus.listen(RxBusEvent.Result::class.java).subscribe {
            if (it.status == "succeeded") {
                invoicesList[it.position].uniqueId = it.id
                invoicesList[it.position].paymentStatus = "PAID"
                adapter.refreshItems(invoicesList)
            }
        }
    }

    private fun executeRecyclerView(view: View) {
        var recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.setLayoutManager(linearLayoutManager)
        recyclerView.setAdapter(adapter)
        adapter.setOnClickListener(this)
        adapter.refreshItems(invoicesList)
    }

    override fun onPayBtnClicked(
        invoiceList: ArrayList<PaymentOrder>,
        position: Int,
        statusView: TextView,
        view: View
    ) {
        var uniqueId = invoiceList[position].uniqueId
        var customerName = invoiceList[position].customerName
        var customerId = invoiceList[position].customerId
        var amount = invoiceList[position].amount
        var paymentStatus = invoiceList[position].paymentStatus
        var cloverOrderId = invoiceList[position].cloverOrderId
        var invoiceId = invoiceList[position].invoiceNo

        var invoiceDetail = InvoiceDetail(
            invoiceId,
            uniqueId,
            customerName,
            customerId,
            amount,
            paymentStatus,
            cloverOrderId,
            position
        )

        showDialog(invoiceDetail)

    }

    private fun showDialog(invoiceDetail: InvoiceDetail) {

        var alertDialog = AlertDialog.Builder(requireContext())
        var customLayout: View = layoutInflater.inflate(R.layout.payment_custom_dialog, null)
        alertDialog.setView(customLayout)
        var alert = alertDialog.create()

        customLayout.findViewById<Button>(R.id.btn_manual).setOnClickListener {
            manualEntryPage(invoiceDetail)
            alert.dismiss()
        }

        customLayout.findViewById<Button>(R.id.btn_swipe).setOnClickListener {
            swipeEntryPage(invoiceDetail)
            alert.dismiss()

        }
        alert.show()

    }

    private fun manualEntryPage(invoiceDetail: InvoiceDetail) {
        var action  = ListOfInvoicesDirections.actionListOfInvoicesToWebViewFragment22(invoiceDetail)
        findNavController().navigate(action)
    }

    private fun swipeEntryPage(invoiceDetail: InvoiceDetail) {
        Toast.makeText(
            requireContext(),
            "Swipe Payment option not supported on Android devices",
            Toast.LENGTH_SHORT
        ).show()
        paymentConnector = MyPaymentConnector(requireContext()).initializePaymentConnector()
        paymentConnector.initializeConnection()
//        onPaymentClick(invoiceDetail)
    }

    fun onPaymentClick(invoiceDetail: InvoiceDetail) {
        var saleRequest: SaleRequest = setupSaleRequest(invoiceDetail)
        paymentConnector.sale(saleRequest)
        //setupPaymentRequest()
        Log.d("TAG", "onItemClick: finish")
    }

    private fun setupSaleRequest(invoiceDetail: InvoiceDetail): SaleRequest {
        // Create a new SaleRequest and populate the required request fields
        val saleRequest = SaleRequest()
        saleRequest.externalId = ExternalIdUtils.generateNewID() //required, but can be any string
        saleRequest.amount = invoiceDetail.amount
        saleRequest.externalId = ExternalIdUtils.generateNewID()
        saleRequest.cardEntryMethods = Intents.CARD_ENTRY_METHOD_ALL
        saleRequest.disablePrinting = true
        saleRequest.disableReceiptSelection = true
        saleRequest.disableDuplicateChecking = true
        saleRequest.tipAmount = 0L
        saleRequest.allowOfflinePayment = false
        saleRequest.tipMode = TipMode.TIP_PROVIDED
        return saleRequest
    }



}