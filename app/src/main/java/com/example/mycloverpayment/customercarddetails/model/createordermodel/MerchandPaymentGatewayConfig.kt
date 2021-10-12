package com.example.mycloverpayment.customercarddetails.model.createordermodel

import com.example.mycloverpayment.customercarddetails.model.createordermodel.KeyInfo

data class MerchandPaymentGatewayConfig(
        val accountName: String,
        val altMid: String,
        val authorizationFrontEnd: String,
        val backendMid: String,
        val frontendMid: String,
        val groupId: String,
        val keyInfo: KeyInfo,
        val keyPrefix: String,
        val mid: String,
        val newBatchCloseEnabled: Boolean,
        val paymentGatewayApi: String,
        val paymentProcessorName: String,
        val production: Boolean,
        val supportsMultiPayToken: Boolean,
        val supportsNakedCredit: Boolean,
        val supportsPreauthOverage: Boolean,
        val supportsTipAdjust: Boolean,
        val supportsTipping: Boolean,
        val tid: String,
        val tokenType: String
)