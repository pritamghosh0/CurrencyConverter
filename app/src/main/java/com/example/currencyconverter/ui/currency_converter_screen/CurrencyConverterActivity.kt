package com.example.currencyconverter.ui.currency_converter_screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.ActivityCurrencyConverterBinding
import com.example.currencyconverter.repository.local.currency_db.CurrencyTable
import com.example.currencyconverter.ui.currency_converter_screen.utility.CurrencyBottomSheet
import com.example.currencyconverter.ui.currency_converter_screen.utility.CurrencyConvertedAdapter
import com.example.currencyconverter.ui.shared_viewmodel.MainViewModel
import com.example.currencyconverter.utils.CommonUtils
import com.example.currencyconverter.utils.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrencyConverterBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: CurrencyConvertedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.white)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        viewModel = ViewModelProvider(
            this@CurrencyConverterActivity
        )[MainViewModel::class.java]

        Glide.with(this).load(R.drawable.ic_spinner).into(binding.imgSpinner)

        val currencyString = intent.getStringExtra(CommonUtils.DATA_SPLASH_TO_CURRENCY)

        if (currencyString != null) {
            val type = object : TypeToken<HashMap<String, Double>>() {}.type
            val currencyMap = Gson().fromJson<HashMap<String, Double>>(currencyString, type)
            val currencyList = currencyMap.keys.toList().sorted()

            // init conversion gridview with old rates
            adapter = CurrencyConvertedAdapter(currencyList.toMutableList(), currencyMap)
            binding.rvConversions.layoutManager =
                GridLayoutManager(this@CurrencyConverterActivity, 3)
            binding.rvConversions.adapter = adapter
            binding.etAmount.addTextChangedListener { text ->
                if (text != null && text.trim().isNotEmpty()) {
                    adapter.setAmount(text.toString().toDouble())
                } else adapter.setAmount(null)
            }

            var lastUpdated: Long = 0
            viewModel.oldCurrencyRate.observe(this) { currencyData ->
                lastUpdated = currencyData?.lastUpdated!!
            }

            // initialize bottom-sheet list with previously stored currencies
            binding.selectorCurrency.setOnClickListener {
                // if conversion rate data is more than 30 minutes old and network is available, then refresh
                val currentTimeMillis = System.currentTimeMillis()
                val thirtyMinutesAgo = currentTimeMillis - (30 * 60 * 1000)
                if ((lastUpdated < thirtyMinutesAgo) && isNetworkAvailable()) {
                    fetchLatestData()
                }
                val bottomSheet = CurrencyBottomSheet(currencyList) { currency ->
                    viewModel.updateSelectedCurrency(currency)
                }
                bottomSheet.show(supportFragmentManager, bottomSheet.sheetTag)
            }

            // refresh conversion gridview with new selected currency
            viewModel.selectedCurrency.observe(this@CurrencyConverterActivity) { selectedCurrency ->
                binding.tvSelectedCurrency.text = selectedCurrency

                if (binding.etAmount.text.trim().isNotEmpty()) {
                    adapter.setSelectedCurrency(selectedCurrency)
                }
            }
        }

        observeCurrencyRates()

    }

    private fun observeCurrencyRates() {
        viewModel.currencyRate.observe(this){ response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.SUCCESS -> {
                    if (response.data?.body() != null && !response.data.body()!!.error) {
                        // update in db
                        val data = CurrencyTable(
                            System.currentTimeMillis(),
                            Gson().toJson(response.data.body()!!.rates)
                        )
                        viewModel.insertCurrencyToDb(data)

                        // refresh list with new rates
                        val currencyMap = response.data.body()!!.rates
                        val currencyList = currencyMap.keys.toList().sorted()
                        adapter.refreshAttrs(currencyList, currencyMap)
                        Toast.makeText(
                            this@CurrencyConverterActivity,
                            getString(R.string.fetched_latest_rates),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    showLoading(false)
                }

                Status.ERROR -> {
                    // conversion gridview will work with previous rate
                    showLoading(false)
                }
            }

        }
    }

    private fun fetchLatestData() {
        viewModel.getExchangeRates(CommonUtils.EXCHANGE_RATE_APP_ID)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.rvConversions.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.imgSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
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
}