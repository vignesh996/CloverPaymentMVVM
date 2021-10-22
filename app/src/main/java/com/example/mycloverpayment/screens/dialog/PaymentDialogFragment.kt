package com.example.mycloverpayment.screens.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseDialog
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent

class PaymentDialogFragment : BaseDialog() {

    companion object {
        private const val DIALOG_MESSAGE = "DIALOG_MESSAGE"
        private const val DIALOG_MANUAL_EVENT = "DIALOG_MANUAL_EVENT"
        private const val DIALOG_SWIPE_EVENT = "DIALOG_SWIPE_EVENT"

        fun getPromptDialog(
                message: String,
                manualEvent: RxBusEvent.DialogEventEnum,
                swipeEvent: RxBusEvent.DialogEventEnum
        ): PaymentDialogFragment {

            var paymentDialogFragment = PaymentDialogFragment()
            val args = Bundle().also {
                it.putString(
                        DIALOG_MESSAGE,
                        message
                )
                it.putSerializable(
                        DIALOG_MANUAL_EVENT,
                        manualEvent
                )
                it.putSerializable(
                        DIALOG_SWIPE_EVENT,
                        swipeEvent
                )
            }
            paymentDialogFragment.arguments = args
            return paymentDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments == null) {
            throw Exception(getString(R.string.can_not_show_dialog_without_argument))
        }

        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.payment_custom_dialog)

        dialog.findViewById<TextView>(R.id.tv_message).text = requireArguments().getString(DIALOG_MESSAGE)

        dialog.findViewById<Button>(R.id.btn_manual).setOnClickListener {
            Log.d("TAG", "onCreateDialog: manual called")

            RxBus.publish(RxBusEvent.EventDialog(
                    requireArguments().getSerializable(DIALOG_MANUAL_EVENT)
                            as RxBusEvent.DialogEventEnum, null
            )
            )
            dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_swipe).setOnClickListener {
            Log.d("TAG", "onCreateDialog: swipe called")
            dismiss()
            RxBus.publish(RxBusEvent.EventDialog(
                    requireArguments().getSerializable(DIALOG_SWIPE_EVENT)
                            as RxBusEvent.DialogEventEnum, null
            )
            )

        }

        return dialog
    }


}