package com.example.currencyconverter.repository.remote.data

import com.google.gson.annotations.SerializedName

data class AllConversionRateResponse(
    @SerializedName("timestamp") var timeStamp: Long,
    var rates: HashMap<String, Double>,
    var base: String,
    var error: Boolean = false,
    var status: Int?,
    var message: String?,
    var description: String?
)