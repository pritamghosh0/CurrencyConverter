package com.example.currencyconverter.repository.remote.data

data class AllCurrenciesResponse(
    var currencyMap: HashMap<String, String>,
    var error: Boolean = false,
    var status: Int?,
    var message: String?,
    var description: String?
)
