package com.example.mycloverpayment.screens.dialog

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.mycloverpayment.rxbus.RxBusEvent

class DialogManager(var context: Context, var fragmentManager: FragmentManager) {

        fun orderPlacementDialog(tag : String) {
        PaymentDialogFragment.getPromptDialog(
            "Choose the Payment manual or swipe?",
            RxBusEvent.DialogEventEnum.MANUAL,
            RxBusEvent.DialogEventEnum.SWIPE ).show(fragmentManager, tag)
    }
}