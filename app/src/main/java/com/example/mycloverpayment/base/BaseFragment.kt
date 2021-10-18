package com.example.mycloverpayment.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<V : ViewDataBinding, out T : BaseViewModel> : Fragment() {

    protected var mDataBinding: V? = null
    private var mViewModel: T? = null

    abstract fun getViewModel(): T?

    abstract fun getBindingVariable(): Int

    abstract fun getContentView(): Int

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mDataBinding = DataBindingUtil.inflate(inflater, getContentView(), container, false)
        performDataBinding()
        return mDataBinding?.root
    }

    private fun performDataBinding() {
        getViewModel()?.let { viewModel ->
            mViewModel = ViewModelProvider(this).get(viewModel::class.java)
            mDataBinding?.setVariable(getBindingVariable(), mViewModel)
            mDataBinding?.lifecycleOwner = this
            mDataBinding?.executePendingBindings()
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(activity?.applicationContext, msg, Toast.LENGTH_LONG).show()
    }

}