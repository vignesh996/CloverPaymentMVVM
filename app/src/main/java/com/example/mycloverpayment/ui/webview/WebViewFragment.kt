package com.example.mycloverpayment.ui.webview


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clover.sdk.util.CloverAuth
import com.example.mycloverpayment.BR
import com.example.mycloverpayment.R
import com.example.mycloverpayment.base.BaseFragment
import com.example.mycloverpayment.carddetails.model.CreateCharge
import com.example.mycloverpayment.databinding.FragmentWebViewBinding
import com.example.mycloverpayment.helper.MainViewModelFactory
import com.example.mycloverpayment.helper.MyJavaScriptInterface
import com.example.mycloverpayment.model.ApisResponse
import com.example.mycloverpayment.model.InvoiceDetail
import com.example.mycloverpayment.rxbus.RxBus
import com.example.mycloverpayment.rxbus.RxBusEvent
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class WebViewFragment : BaseFragment<FragmentWebViewBinding, WebViewViewModel>() {

    lateinit var webViewBinding: FragmentWebViewBinding
    private val args: WebViewFragmentArgs by navArgs()
    lateinit var invoiceDetail: InvoiceDetail
    private var webView: WebView? = null
    private var authResult: CloverAuth.AuthResult? = null
    private lateinit var dataDisposable: Disposable
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }

    override fun getViewModel(): WebViewViewModel? =
            ViewModelProvider(this, factory).get(WebViewViewModel::class.java)

    override fun getBindingVariable(): Int = BR.webViewViewModel

    override fun getContentView(): Int = R.layout.fragment_web_view

    override fun onStart() {
        super.onStart()
        // Start receiving the message
        requireContext().registerReceiver(updateTokenReceiver, IntentFilter("ACTION_UPDATE_TOKEN"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webViewBinding = mDataBinding!!
        invoiceDetail = args.invoiceDetail

        // Listen for MessageEvents only
        tokenObserver()
        // Display web view
        webViewDisplay()

    }

    private fun webViewDisplay(){
        webView = webViewBinding.webView
        webView?.settings?.javaScriptEnabled = true
        webView?.addJavascriptInterface(MyJavaScriptInterface(requireContext()), "Android")
        loadWebview("http://192.168.1.6:8080/myapp/webview.html")
        webView?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN){
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    if (webView != null){
                        if (webView!!.canGoBack()){
                            webView!!.goBack()
                        }else{
                            activity?.onBackPressed()
                        }
                    }
                }
            }
            return@setOnKeyListener true
        }
    }

    private fun tokenObserver() {
        dataDisposable = RxBus.listen(RxBusEvent.Token::class.java).subscribe {
            val intent = Intent("ACTION_UPDATE_TOKEN")
            intent.putExtra("token", it.token)
            requireContext().sendBroadcast(intent)
        }
    }

    private fun loadWebview(url: String) {

        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                showToast("Got Error! $error")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }

        webView?.loadUrl(url)

    }

    private val updateTokenReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            showProgressBar()
            GlobalScope.launch(Dispatchers.Main) {
                authResult = getViewModel()?.getCloverAuth()
                var cardToken = intent.getStringExtra("token")
                var amount = invoiceDetail.amount.toInt() * 100
                createCharge(CreateCharge(amount, "usd", "ecom", cardToken!!))
            }
        }
    }

    private fun createCharge(createCharge: CreateCharge) {

        var authToken = "Bearer " + authResult?.authToken
        getViewModel()?.createCharge(authToken, createCharge)?.observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    webView?.destroy()
                    findNavController().navigate(R.id.action_webViewFragment2_to_listOfInvoices)
                    RxBus.publish(RxBusEvent.Result(apiResponse.response.id, apiResponse.response.status, invoiceDetail.position))
                    hideProgressBar()
                    getViewModel()?.toastMessage?.value =getString(R.string.payment_sucessfull)
                    getViewModel()?.toastMessage?.observe(viewLifecycleOwner, Observer {
                        showToast(it)
                    })
                }
                is ApisResponse.CustomError -> {
                    hideProgressBar()
                    getViewModel()?.toastMessage?.value = apiResponse.message
                    getViewModel()?.toastMessage?.observe(viewLifecycleOwner, Observer {
                        showToast(it)
                    })
                }
            }
        })
    }

    private fun showProgressBar() {
        webViewBinding.progressbarLayout.visibility = View.VISIBLE
        webViewBinding.webView.visibility = View.INVISIBLE
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun hideProgressBar() {
        webViewBinding.progressbarLayout.visibility = View.GONE
        webViewBinding.webView.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        if (!dataDisposable.isDisposed) dataDisposable.dispose()
    }

    override fun onStop() {
        // Stop receiving the message
        requireContext().unregisterReceiver(updateTokenReceiver)
        super.onStop()
    }
}