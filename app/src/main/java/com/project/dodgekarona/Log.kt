package com.project.dodgekarona

import android.util.Log

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
object Log {
    val LOG = BuildConfig.DEBUG

    fun i(tag: String, string: String) {
        if (LOG) Log.i(tag, string)
    }

    fun e(tag: String, string: String) {
        if (LOG) Log.e(tag, string)
    }

    fun d(tag: String, string: String) {
        if (LOG) Log.d(tag, string)
    }

    fun v(tag: String, string: String) {
        if (LOG) Log.v(tag, string)
    }

    fun w(tag: String, string: String) {
        if (LOG) Log.w(tag, string)
    }
}