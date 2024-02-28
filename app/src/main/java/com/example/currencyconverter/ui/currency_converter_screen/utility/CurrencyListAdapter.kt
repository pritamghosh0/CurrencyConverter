package com.example.currencyconverter.ui.currency_converter_screen.utility

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R

class CurrencyListAdapter(
    private val items: MutableList<String>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<String>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount()= items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        vh.tvCurrency.text = items[position]
        vh.itemView.setOnClickListener {
            onItemSelected(items[position])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvCurrency: TextView
        init {
            tvCurrency = view.findViewById(R.id.tv_currency)
        }
    }
}