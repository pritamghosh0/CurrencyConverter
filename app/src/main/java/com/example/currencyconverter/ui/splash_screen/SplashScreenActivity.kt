package com.example.currencyconverter.ui.splash_screen

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.SplashScreenBinding
import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.ui.currency_converter_screen.CurrencyConverterActivity
import com.example.currencyconverter.ui.shared_viewmodel.MainViewModel
import com.example.currencyconverter.utils.Status
import com.example.currencyconverter.utils.CommonUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.white)

        Glide.with(this).load(R.drawable.ic_money).into(binding.imgMoney)
        Glide.with(this).load(R.drawable.ic_loader).into(binding.imgLoader)

        viewModel = ViewModelProvider(
            this@SplashScreenActivity)[MainViewModel::class.java]
        fetchCurrencyData()
    }

    // Method to check if conversion rates should be refreshed from API
    private fun shouldRefreshData(lastUpdated: Long?): Boolean{
        val currentTimeMillis = System.currentTimeMillis()
        val thirtyMinutesAgo = currentTimeMillis - (30 * 60 * 1000)
        // if database was updated more than 30 minutes ago or the application is starting for the first time
        return (lastUpdated==null || lastUpdated<thirtyMinutesAgo)
    }


    // Method to fetch currency conversion ratios
    private fun fetchCurrencyData() {
        viewModel.oldCurrencyRate.observe(this) { prevCurrencyData ->

            if (prevCurrencyData == null || shouldRefreshData(prevCurrencyData.lastUpdated)) {
                viewModel.getExchangeRates(CommonUtils.EXCHANGE_RATE_APP_ID)
                observeExchangeRates(prevCurrencyData)
            } else {
                val intent = Intent(
                    this@SplashScreenActivity, CurrencyConverterActivity::class.java
                )
                intent.putExtra(
                    CommonUtils.DATA_SPLASH_TO_CURRENCY, prevCurrencyData.conversionRate
                )
                startActivity(
                    intent
                )
                finish()
            }

        }
    }

    // Method to observe exchange rate
    private fun observeExchangeRates(prevCurrencyData: CurrencyTable?){
        // observe forever won't cause any issue, coz moving to new screen once success/error response is received
        // observer will be removed once new screen launches
        viewModel.currencyRate.observe(this){ response ->
            when (response.status) {
                Status.LOADING -> {
                    // stay on splash screen
                }

                Status.SUCCESS -> {
                    if (response.data?.body() != null && !response.data.body()!!.error) {
                        val data = CurrencyTable(
                            System.currentTimeMillis(),
                            Gson().toJson(response.data.body()!!.rates)
                        )
                        viewModel.insertCurrencyToDb(data)
                        val intent = Intent(
                            this@SplashScreenActivity,
                            CurrencyConverterActivity::class.java
                        )
                        intent.putExtra(
                            CommonUtils.DATA_SPLASH_TO_CURRENCY,
                            response.data.body()!!.rates
                        )
                        startActivity(
                            intent
                        )
                    }
                    val intent = Intent(
                        this@SplashScreenActivity, CurrencyConverterActivity::class.java
                    )
                    intent.putExtra(
                        CommonUtils.DATA_SPLASH_TO_CURRENCY,
                        prevCurrencyData?.conversionRate ?: ""
                    )
                    startActivity(
                        intent
                    )
                    finish()
                }

                Status.ERROR -> {
                    // If previously data is present in DB then launch main screen otherwise show error
                    if (prevCurrencyData?.conversionRate != null) {
                        val intent = Intent(
                            this@SplashScreenActivity,
                            CurrencyConverterActivity::class.java
                        )
                        intent.putExtra(
                            CommonUtils.DATA_SPLASH_TO_CURRENCY,
                            prevCurrencyData.conversionRate
                        )
                        startActivity(
                            intent
                        )
                        finish()
                    } else {
                        if (isNetworkAvailable()) {
                            // undetected error
                            Toast.makeText(
                                this@SplashScreenActivity,
                                getString(R.string.error_occurred),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // register for network connectivity and fetch once network is available
                            Toast.makeText(
                                this@SplashScreenActivity,
                                getString(R.string.network_unavailable),
                                Toast.LENGTH_SHORT
                            ).show()
                            val connectivityManager =
                                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                            val networkRequest = NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .build()
                            connectivityManager.registerNetworkCallback(
                                networkRequest, networkCallback
                            )
                        }
                    }
                }
            }

        }
    }

    // Check for connectivity
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            runOnUiThread {
                fetchCurrencyData()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // network callback wasn't registered
        }
    }
}