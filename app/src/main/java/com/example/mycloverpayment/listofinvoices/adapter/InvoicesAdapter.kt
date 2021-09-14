package com.example.mycloverpayment.listofinvoices.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloverpayment.R
import com.example.mycloverpayment.model.PaymentOrder

class InvoicesAdapter : RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder>() {

    private var invoiceList = ArrayList<PaymentOrder>()
    private var onClickListener: OnServiceClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        var itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.invoices_card_view, parent, false)
        return InvoiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.onBind(invoiceList[position])

        var statusView = holder.itemView.findViewById<TextView>(R.id.payment_status)
        holder.itemView.setOnClickListener {
            onClickListener?.onClickInvoice(invoiceList, position, statusView)
        }
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }

    inner class InvoiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var customerName = view.findViewById<TextView>(R.id.customer_name)
        var customerId = view.findViewById<TextView>(R.id.customer_id)
        var invoiceAmont = view.findViewById<TextView>(R.id.invoice_amount)
        var uniqueId = view.findViewById<TextView>(R.id.unique_id)
        var paymentStatus = view.findViewById<TextView>(R.id.payment_status)

        fun onBind(invoice: PaymentOrder) {
            uniqueId.text = invoice.uniqueId
            customerName.text = invoice.customerName
            customerId.text = invoice.customerId
            invoiceAmont.text = invoice.amount.toString()
            paymentStatus.text = invoice.paymentStatus
            if (invoice.paymentStatus.toUpperCase() == "PAID") {
                paymentStatus.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                paymentStatus.setTextColor(Color.parseColor("#E1AF1B"))
            }

        }
    }

    fun refreshItems(invoice: List<PaymentOrder>) {
        invoiceList.clear()
        invoiceList.addAll(invoice)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnServiceClickListener) {
        onClickListener = listener
    }

    interface OnServiceClickListener {
        fun onClickInvoice(invoiceList: ArrayList<PaymentOrder>, position: Int, statusView: TextView)

    }

}