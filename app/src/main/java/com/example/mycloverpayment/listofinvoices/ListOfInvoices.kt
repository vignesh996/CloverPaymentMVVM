package com.example.mycloverpayment.listofinvoices

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.databinding.FragmentListOfInvoicesBinding
import com.example.mycloverpayment.listofinvoices.adapter.InvoicesAdapter
import com.example.mycloverpayment.model.PaymentOrder
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent


class ListOfInvoices : Fragment(), InvoicesAdapter.OnServiceClickListener {

    lateinit var mViewModel: ListOfInvoicesViewModel
    lateinit var mDataBinding: FragmentListOfInvoicesBinding
    var adapter = InvoicesAdapter()
    lateinit var invoicesList : List<PaymentOrder>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        RxBus.listen(RxBusEvent.InvoiceDetail::class.java).subscribe {
            Log.d("TAG", "onViewCreated: ${it.invoiceDetail.paymentStatus}")
            Log.d("TAG", "onViewCreated: ${it.invoiceDetail.uniqueId}")
            invoicesList[it.position].uniqueId = it.invoiceDetail.uniqueId
            adapter.refreshItems(invoicesList)
        }
    }

    private fun observePaymentStatus(){
        RxBus.listen(RxBusEvent.PaymentStatus::class.java).subscribe {
            Log.d("TAG", "onViewCreated: ${it.invoiceDetail.paymentStatus}")
            invoicesList[it.position].paymentStatus = it.invoiceDetail.paymentStatus
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

        invoicesList.add(PaymentOrder("454","Vignesh","78945",500L,"open"))
        invoicesList.add(PaymentOrder("589","Arun","78946",700L,"open"))
        invoicesList.add(PaymentOrder("987","Suresh","78947",900L,"open"))
        invoicesList.add(PaymentOrder("354","Jaya","78948",1500L,"open"))
        invoicesList.add(PaymentOrder("986","Kamal","78949",5000L,"open"))
        invoicesList.add(PaymentOrder("234","Partha","78910",2500L,"open"))
        invoicesList.add(PaymentOrder("666","Murty","78911",7500L,"open"))
        invoicesList.add(PaymentOrder("222","Vijay","78912",9500L,"open"))
        invoicesList.add(PaymentOrder("111","Magesh","78913",6500L,"open"))
        invoicesList.add(PaymentOrder("444","Muthu","78914",700L,"open"))
        invoicesList.add(PaymentOrder("333","Surya","78915",500L,"open"))
        invoicesList.add(PaymentOrder("555","Latchu","78916",800L,"open"))
        invoicesList.add(PaymentOrder("777","Mugu","78917",200L,"open"))

        return invoicesList
    }

    private fun executeDataBindingAndViewModel() {
        mViewModel = ViewModelProvider(this).get(ListOfInvoicesViewModel::class.java)
        mDataBinding.setVariable(BR.listOfInvoicesViewModel, mViewModel)
        mDataBinding.lifecycleOwner = this
        mDataBinding.executePendingBindings()
    }


    override fun onClickInvoice(invoiceList: ArrayList<PaymentOrder>, position: Int, statusView: TextView) {
        var uniqueId = invoiceList[position].uniqueId
        var customerName = invoiceList[position].customerName
        var customerId = invoiceList[position].customerId
        var amount = invoiceList[position].amount
        var paymentStatus = invoiceList[position].paymentStatus


        var invoiceDetails =  PaymentOrder(uniqueId,customerName,customerId,amount,paymentStatus)

        RxBus.publish(RxBusEvent.Details(invoiceDetails, position))

    }
}