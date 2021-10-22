package com.example.mycloverpayment.base

import androidx.fragment.app.DialogFragment

abstract class BaseDialog: DialogFragment()  {

    companion object {
        enum class ScreenState {
            DIALOG_IDLE, DIALOG_SHOWN, DIALOG_APP_EXIT
        }
    }

    fun getMyString(id:Int) : String {
        return requireContext().getString(id)
    }
}