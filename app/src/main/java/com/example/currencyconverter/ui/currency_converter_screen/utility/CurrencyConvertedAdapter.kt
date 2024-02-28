package com.example.currencyconverter.ui.currency_converter_screen.utility

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R

class CurrencyConvertedAdapter(
    private val currencies: MutableList<String>,
    private val rates: HashMap<String, Double>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var selectedCurrency = "USD"
    private var amount: Double? = null

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAttrs(newCurrencies: List<String>, newRates: HashMap<String, Double>){
        currencies.clear()
        currencies.addAll(newCurrencies)
        rates.clear()
        rates.putAll(newRates)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedCurrency(currency: String){
        selectedCurrency = currency
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAmount(amt: Double?){
        amount = amt
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency_converted, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        val currency = currencies[position]
        vh.tvCurrency.text = currency
        if(amount==null)
            vh.tvAmount.text = "-"
        else{
            // toBeConverted/selected*amount
            val resAmount = rates.getOrDefault(currency, 0.0)/ rates.getOrDefault(selectedCurrency, 1.0 )* amount!!
            vh.tvAmount.text = String.format("%.2f", resAmount)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvCurrency: TextView
        val tvAmount: TextView
        init {
            tvCurrency = view.findViewById(R.id.tv_currency)
            tvAmount = view.findViewById(R.id.tv_amount)
        }
    }
}