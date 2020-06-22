package com.project.dodgecorona

import java.util.*

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun Locale.isMetric(): Boolean {
    Log.i(TAG, "isMetric: ${country.toUpperCase(Locale.getDefault())}")
    return when (country.toUpperCase(Locale.getDefault())) {
        //US, Liberia, Myanmar, UK
        "US", "LR", "MM", "GB" -> false
        else -> true
    }
}