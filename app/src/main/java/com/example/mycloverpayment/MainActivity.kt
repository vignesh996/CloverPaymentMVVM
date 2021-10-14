package com.example.mycloverpayment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.mycloverpayment.base.BaseActivity
import com.example.mycloverpayment.databinding.ActivityMainBinding
import com.example.mycloverpayment.rxbus.RxBus

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    lateinit var navController: NavController

    override fun getContentView(): Int = R.layout.activity_main

    override fun getViewModel(): MainViewModel? =
        ViewModelProvider(this).get(MainViewModel::class.java)

    override fun getBindingVariable(): Int = BR.mainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       navController =  Navigation.findNavController(this,R.id.fragments)
        NavigationUI.setupActionBarWithNavController(this,navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()

        return super.onSupportNavigateUp()
    }

}