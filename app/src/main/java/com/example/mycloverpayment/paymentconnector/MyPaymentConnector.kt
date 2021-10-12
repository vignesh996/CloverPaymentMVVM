package com.example.mycloverpayment.paymentconnector

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.clover.connector.sdk.v3.PaymentConnector
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.v3.connector.IPaymentConnectorListener
import com.clover.sdk.v3.remotepay.*
import com.example.mycloverpayment.BuildConfig

class MyPaymentConnector(private val context: Context)  {

    fun initializePaymentConnector(): PaymentConnector {
        // Get the Clover account that will be used with the service; uses the GET_ACCOUNTS permission
        val cloverAccount: android.accounts.Account? = CloverAccount.getAccount(context)
        // Set your RAID as the remoteApplicationId
        val remoteApplicationId = BuildConfig.RAID

        // Create the PaymentConnector with the context, account, listener, and RAID
        return PaymentConnector(
                context,
                cloverAccount,
                iPaymentServiceListener,
                remoteApplicationId
        )
    }

    private var iPaymentServiceListener = object : IPaymentConnectorListener {
        override fun onDeviceDisconnected() {
            Toast.makeText(context, "Device Disconnected", Toast.LENGTH_SHORT).show()
        }

        override fun onDeviceConnected() {
            Log.d("TAG", "Payment Connector Device Connected method called")
        }


        override fun onPreAuthResponse(p0: PreAuthResponse?) {
            Toast.makeText(context, "onPreAuthResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthResponse(p0: AuthResponse?) {
            Toast.makeText(context, "onAuthResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onTipAdjustAuthResponse(p0: TipAdjustAuthResponse?) {
            Toast.makeText(context, "onTipAdjustAuthResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onCapturePreAuthResponse(p0: CapturePreAuthResponse?) {
            Toast.makeText(context, "onCapturePreAuthResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onVerifySignatureRequest(p0: VerifySignatureRequest?) {
            Toast.makeText(context, "onVerifySignatureRequest", Toast.LENGTH_SHORT).show()
        }

        override fun onConfirmPaymentRequest(p0: ConfirmPaymentRequest?) {
            Toast.makeText(context, "onConfirmPaymentRequest", Toast.LENGTH_SHORT).show()
        }

        override fun onSaleResponse(response: SaleResponse) {
            Log.d("TAG", "Payment Connector SaleResponse method called")
            Toast.makeText(context, "onSaleResponse", Toast.LENGTH_SHORT).show()
            val result: String = if (response.success) {
                "Sale was successful"
            } else {
                "Sale was unsuccessful" + response.reason + ":" + response.message
            }
            Toast.makeText(
                    context,
                    result,
                    Toast.LENGTH_LONG
            ).show()
        }

        override fun onManualRefundResponse(p0: ManualRefundResponse?) {
            Toast.makeText(context, "onManualRefundResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onRefundPaymentResponse(p0: RefundPaymentResponse?) {
            Toast.makeText(context, "onRefundPaymentResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onTipAdded(p0: TipAdded?) {
            Toast.makeText(context, "onTipAdded", Toast.LENGTH_SHORT).show()
        }

        override fun onVoidPaymentResponse(p0: VoidPaymentResponse?) {
            Toast.makeText(context, "onVoidPaymentResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onVaultCardResponse(p0: VaultCardResponse?) {
            Toast.makeText(context, "onVaultCardResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onRetrievePendingPaymentsResponse(p0: RetrievePendingPaymentsResponse?) {
            Toast.makeText(context, "onRetrievePendingPaymentsResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onReadCardDataResponse(p0: ReadCardDataResponse?) {
            Toast.makeText(context, "onReadCardDataResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onRetrievePaymentResponse(p0: RetrievePaymentResponse?) {
            Toast.makeText(context, "onRetrievePaymentResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onCloseoutResponse(p0: CloseoutResponse?) {
            Toast.makeText(context, "onCloseoutResponse", Toast.LENGTH_SHORT).show()
        }

        override fun onVoidPaymentRefundResponse(p0: VoidPaymentRefundResponse?) {
            Toast.makeText(context, "onVoidPaymentRefundResponse", Toast.LENGTH_SHORT).show()
        }

    }
}