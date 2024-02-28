package com.example.currencyconverter.ui.currency_converter_screen.utility

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CurrencyBottomSheet(
    private val items: List<String>,
    private val onItemSelected: (String) -> Unit
) : BottomSheetDialogFragment(){
    private lateinit var binding: LayoutBottomSheetBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutBottomSheetBinding.inflate(inflater, container, false)

        // changing softInputMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setOnApplyWindowInsetsListener(requireDialog().window?.decorView!!) { _, insets ->
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val navigationBarHeight =
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                binding.root.setPadding(48, 32, 48, imeHeight - navigationBarHeight)
                insets
            }
        }else{
            @Suppress("DEPRECATION")
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CurrencyListAdapter(items.toMutableList()){ currency->
            onItemSelected(currency)
            dismiss()
        }
        binding.rvCurrencies.layoutManager = LinearLayoutManager(context)
        binding.rvCurrencies.adapter = adapter

        // filter items on search, if no item found show no data text
        binding.etSearch.addTextChangedListener { text ->
            binding.tvNoData.visibility = View.GONE
            binding.rvCurrencies.visibility = View.VISIBLE
            val filteredItems = items.filter { it.contains(text.toString(), ignoreCase = true) }
            if(filteredItems.isEmpty()){
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvCurrencies.visibility = View.GONE
            }else{
                adapter.setItems(filteredItems)
                binding.rvCurrencies.smoothScrollToPosition(0)
            }
        }
    }

    val sheetTag = "currency_bottom_sheet"
}