package com.example.mycloverpayment.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<out T : ViewDataBinding, out V : BaseViewModel> : AppCompatActivity(){

    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null

    abstract fun getContentView(): Int

    abstract fun getViewModel(): V?

    fun getViewDataBinding(): T? {
        return mViewDataBinding
    }

    abstract fun getBindingVariable(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getContentView())
        mViewModel = getViewModel()
        mViewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding?.executePendingBindings()
    }

}