package com.example.mycloverpayment.ui.listofinvoices

import android.accounts.Account
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clover.connector.sdk.v3.PaymentConnector
import com.clover.sdk.v1.Intents
import com.clover.sdk.v3.connector.ExternalIdUtils
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
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import com.example.mycloverpayment.screens.dialog.DialogManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class ListOfInvoices : BaseFragment<FragmentListOfInvoicesBinding, ListOfInvoicesViewModel>(),
        InvoicesAdapter.OnServiceClickListener {

    lateinit var listOfInvoicesBinding: FragmentListOfInvoicesBinding
    @Inject
    lateinit var adapter: InvoicesAdapter
    private var mAccount: Account? = null
    private lateinit var paymentConnector: PaymentConnector
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var invoiceDetail : InvoiceDetail? = null
    private var pressedTime :Long = 0

    private lateinit var dialogDisposable: Disposable
    private lateinit var dialogManager: DialogManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }

    override fun getViewModel(): ListOfInvoicesViewModel? =
            ViewModelProvider(this, factory).get(ListOfInvoicesViewModel::class.java)

    override fun getBindingVariable(): Int = BR.listOfInvoicesViewModel

    override fun getContentView(): Int = R.layout.fragment_list_of_invoices

    companion object {
        private var invoicesList = StaticInvoiceList().getInvoiceList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Method for restrict user back button
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (pressedTime + 1000 > System.currentTimeMillis()) {
                requireActivity().finish()
            }
            else {
                showToast(getString(R.string.Press_back_again_to_exit))
            }
            pressedTime = System.currentTimeMillis()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        listOfInvoicesBinding = mDataBinding!!

        // DialogManager class instantiation
        dialogManager = DialogManager(requireContext(), requireActivity().supportFragmentManager)
        // Get current account
        getCurrentAccount()
        // Adapting recyclerView
        executeRecyclerView(view)
        // Update ResultData
        observeResult()
        // Observe Payment Option
        observeDialogResult()

    }

    private fun getCurrentAccount() {
        mAccount = CloverPaymentApp().mAccount
    }

    private fun observeResult() {
        dialogDisposable = RxBus.listen(RxBusEvent.Result::class.java).subscribe {
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

         invoiceDetail = InvoiceDetail(
            invoiceId,
            uniqueId,
            customerName,
            customerId,
            amount,
            paymentStatus,
            cloverOrderId,
            position
        )

        dialogManager.orderPlacementDialog("payment")

    }

    private fun  observeDialogResult() {

        dialogDisposable = RxBus.listen(RxBusEvent.EventDialog::class.java).subscribe {
            when (it.dialogEvent) {
                RxBusEvent.DialogEventEnum.MANUAL -> {
                    manualEntryPage()
                }
                RxBusEvent.DialogEventEnum.SWIPE -> {
                    swipeEntryPage()
                }
            }
        }
    }


    private fun manualEntryPage() {
        try {
                var action  =  ListOfInvoicesDirections.actionListOfInvoicesToWebViewFragment22(invoiceDetail!!)
                if (action != null) {
                    findNavController().navigate(action)
                    onDestroy()
                }
        }catch (e: Exception){
            Log.d("TAG", "manualEntryPage: ${e.message} ")
        }

    }

    private fun swipeEntryPage() {
        getViewModel()?.toastMessage?.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
//        paymentConnector = MyPaymentConnector(requireContext()).initializePaymentConnector()
//        paymentConnector.initializeConnection()
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

    override fun onDestroy() {
        super.onDestroy()
        if (!dialogDisposable.isDisposed) dialogDisposable.dispose()
    }

}