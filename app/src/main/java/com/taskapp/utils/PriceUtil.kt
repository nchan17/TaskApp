package com.taskapp.utils

import java.text.DecimalFormat

object PriceUtil {
    fun getPrice(num: Double?, currency: String = "₾"): String {
        val formatPrice = DecimalFormat("###,###,##0.00")
        formatPrice.maximumFractionDigits = 2
        return formatPrice.format(num) + " " + currency
    }
}