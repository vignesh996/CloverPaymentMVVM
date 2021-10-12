package com.example.mycloverpayment.customercarddetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.clover.sdk.util.CloverAuth
import com.clover.sdk.v3.employees.EmployeeConnector
import com.example.mycloverpayment.customercarddetails.model.createordermodel.CreateCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerCardDetailsViewModel : ViewModel() {


    suspend fun getEmployeeDetailsByEmployeeConnector(employeeConnector: EmployeeConnector?) {

        return withContext(Dispatchers.IO) {
            employeeConnector!!.employee
            Log.d("TAG", "getEmployeeDetails: ${employeeConnector!!.employee}")
            Log.d("TAG", "getEmployeeDetails: ${employeeConnector!!.employee.id}")
        }
    }

    suspend fun getCloverAuth(context: Context) : CloverAuth.AuthResult? {

        return withContext(Dispatchers.IO) {
            try {
                return@withContext  CloverAuth.authenticate(context.applicationContext)
            } catch (e: Exception) {
                Log.d("TAG", "Error authenticating", e)
            }
            return@withContext null
        }
    }

     fun getMerchantPaymentGateway(authToken :String,mId: String) = liveData(Dispatchers.IO) {
        emit(CustomerCardDetailsRepository().getMerchantPaymentGateway(authToken, mId))
    }

    fun createCustomer(authToken :String,mId: String,createCustomer: CreateCustomer) = liveData(Dispatchers.IO) {
        emit(CustomerCardDetailsRepository().createCustomer(authToken,mId,createCustomer))
    }

}