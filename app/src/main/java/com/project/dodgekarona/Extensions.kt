package com.project.dodgekarona

import java.util.*

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun Locale.isMetric(): Boolean {
    Log.i(TAG, "isMetric: ${country.uppercase(Locale.getDefault())}")
    return when (country.uppercase(Locale.getDefault())) {
        //US, Liberia, Myanmar, UK
        "US", "LR", "MM", "GB" -> false
        else -> true
    }
}